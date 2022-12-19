package bzl.processRequest;

import entity.Request;
import entity.RequestDeserializer;
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
    private static BlockingQueue<Request> requestQ;
    private static Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    private volatile static List<Future<RequestResponse>> futureList = new ArrayList<Future<RequestResponse>>();

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
        //System.out.println(req);
        var request = RequestDeserializer.toRequest(req);
        requestQ.put(request);
    }

    public void sendRequestsForProcessing() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newCachedThreadPool();


        for (Request r : requestQ) {
            if (!processingMap.containsKey(r.getCompanyId())) {
                System.out.println("Following request has been sent for processing: ");
                System.out.println(r);
                log.info("Following request has been sent for processing: ");
                log.info(r);
                futureList.add(executorService.submit(new ProcessRequest(r, repo)));
                processingMap.put(r.getCompanyId(), true);
                requestQ.remove(r);
            }
            System.out.println("FUTURE LIST len " + futureList.size());
            for(Future f: futureList)
            {
                if (f.isDone())
                {
                    System.out.println("Request has been processed!");
                    RequestResponse resp = (RequestResponse) f.get();
                    processingMap.remove(resp.getCompanyId());
                    futureList.remove(f);

                }
            }
        }

        executorService.shutdown();
        System.out.println("AAAa " + futureList.size());
        for(Future f: futureList)
        {
            if (f.isDone())
            {
                System.out.println("Request has been processed!2");
                RequestResponse resp = (RequestResponse) f.get();
                processingMap.remove(resp.getCompanyId());
                futureList.remove(f);
            }
        }
    }
}