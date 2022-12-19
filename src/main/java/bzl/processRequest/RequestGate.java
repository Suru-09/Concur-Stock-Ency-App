package bzl.processRequest;

import entity.Request;
import entity.RequestDeserializer;
import entity.ValueCalculator;
import entity.RequestResponse;
import exceptions.RepoException;
import jdk.swing.interop.SwingInterOpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repo.GSONRepo;

import java.util.*;
import java.util.concurrent.*;

public class RequestGate {

    private volatile static RequestGate instance;
    private static volatile BlockingQueue<Request> requestQ;
    private static volatile Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    private ValueCalculator valueCalc;
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

    public static RequestGate getInstance()
    {
        RequestGate ref = instance;
        if (ref == null)
        {
            synchronized (RequestGate.class) {
                ref = instance;
                if (ref == null) {
                    instance = ref = new RequestGate();
                }
            }
        }
        return ref;
    }

    public void addRequest(String req) throws InterruptedException {
        System.out.println("Adaug request");
        log.info("Adaug request");
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
                futureList.add(executorService.submit(new ProcessRequest(r, repo)));
                processingMap.put(r.getCompanyId(), true);
                iterator.remove();
            }

            iterateFutureList();
        }


        executorService.shutdown();
        iterateFutureList();
    }

    private void iterateFutureList() {
        for(int i = 0; i < futureList.size(); ++i)
        {
            var f = futureList.get(i);
            if (f.isDone())
            {
                try {
                    System.out.println("Request has been processed!");
                    log.info("Request has been processed!");
                    RequestResponse resp = f.get(2, TimeUnit.SECONDS);
                    processingMap.remove(resp.getCompanyId());
                    futureList.remove(f);
                    --i;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}