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
    private static volatile ConcurrentLinkedQueue<Request> requestQ;
    private static volatile Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    static volatile List<Future<RequestResponse>> futureList = new ArrayList<Future<RequestResponse>>();

    private static final Logger log = LogManager.getLogger("Main");

    private RequestGate() {
        requestQ = new ConcurrentLinkedQueue<Request>();
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

    public synchronized void addRequest(String req) throws InterruptedException {
        System.out.println("Adaug request");
        //System.out.println(req);
        var request = RequestDeserializer.toRequest(req);
        requestQ.add(request);
    }

    public void sendRequestsForProcessing() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        System.out.println("Size of requestQ: " + requestQ.size());
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
//            for(Future<RequestResponse> f: futureList)
//            {
//                if (f.isDone())
//                {
//                    System.out.println("Request has been processed!");
//                    try {
//                        RequestResponse resp = (RequestResponse) f.get(2, TimeUnit.SECONDS);
//                        processingMap.remove(resp.getCompanyId());
//                        futureList.remove(f);
//                        processingMap.clear();
//                    }
//                    catch (Exception e) {
//                        System.out.println("MORTII MA-TIII");
//                        e.printStackTrace();
//                    }
//                }
//            }
        }

        executorService.shutdown();
        System.out.println("AAAa " + futureList.size());
        for(int i = 0; i < futureList.size(); ++i)
        {
            var f = futureList.get(i);
            System.out.println(f);
            if (f.isDone())
            {
                try {
                    System.out.println("Request has been processed!2");
                    RequestResponse resp = f.get(2, TimeUnit.SECONDS);
                    processingMap.remove(resp.getCompanyId());
                    futureList.remove(f);
                    --i;
                }
                catch (Exception e) {
                    System.out.println("MORTII MA-TIII");
                    e.printStackTrace();
                }
            }
        }
        System.out.println("AAAa " + futureList.size());
    }
}