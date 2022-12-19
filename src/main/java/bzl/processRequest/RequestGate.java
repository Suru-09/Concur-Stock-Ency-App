package bzl.processRequest;

import bzl.event.RabbitEventT;
import com.rabbitmq.client.ConnectionFactory;
import entity.Request;
import entity.RequestDeserializer;
import entity.ValueCalculator;
import entity.RequestResponse;
import exceptions.RepoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rabbitMQ.ChannelsPool;
import repo.GSONRepo;

import java.util.*;
import java.util.concurrent.*;

public class RequestGate {

    private volatile static RequestGate instance;
    private static volatile BlockingQueue<Request> requestQ;
    private static volatile Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    private ValueCalculator valueCalc;
    private static volatile ChannelsPool channelsPool;
    static volatile List<Future<RequestResponse>> futureList = new ArrayList<Future<RequestResponse>>();

    private static final Logger log = LogManager.getLogger("Main");

    private RequestGate() {
        requestQ = new LinkedBlockingQueue<Request>();
        processingMap = new ConcurrentHashMap<Long, Boolean>();
        try {
            repo = GSONRepo.getInstance();
        }
        catch(RepoException e) {
            e.printStackTrace();
        }
        this.valueCalc = new ValueCalculator(this.repo);
    }

    public static RequestGate getInstance(ChannelsPool channelsPool)
    {
        RequestGate ref = instance;
        if (ref == null)
        {
            synchronized (RequestGate.class) {
                ref = instance;
                if (ref == null) {
                    RequestGate.channelsPool = channelsPool;
                    instance = ref = new RequestGate();
                }
            }
        }
        return ref;
    }

    public void addRequest(String req) throws InterruptedException {
        System.out.println("Adaug request");
        var request = RequestDeserializer.toRequest(req);
        requestQ.put(request);
    }

    public void sendRequestsForProcessing() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        System.out.println("Size of requestQ: " + requestQ.size());
        Iterator<Request> iterator = requestQ.iterator();
        while ( iterator.hasNext() )
        {
            var r = iterator.next();
            if (!processingMap.containsKey(r.getCompanyId())) {
                System.out.println("Following request has been sent for processing: ");
                System.out.println(r);
                log.info("Following request has been sent for processing: ");
                log.info(r);
                futureList.add(executorService.submit(new ProcessRequest(r, repo, valueCalc)));
                processingMap.put(r.getCompanyId(), true);
                iterator.remove();
            }
            iterateFutureList();
        }

        executorService.shutdown();
        iterateFutureList();
    }

    private void iterateFutureList() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0; i < futureList.size(); ++i)
        {
            var f = futureList.get(i);
            if (f.isDone())
            {
                try {
                    System.out.println("Request has been processed!");
                    RequestResponse resp = f.get(2, TimeUnit.SECONDS);
                    log.info("Request has been processed for company id: [" + resp.getCompanyId() + "] and client id: [" + resp.getClientId() + "]");
                    RabbitEventT event = new RabbitEventT(resp.toString(), "Client", channelsPool);
                    executorService.submit(event);
                    processingMap.remove(resp.getCompanyId());
                    futureList.remove(f);
                    --i;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        executorService.shutdown();
    }
}