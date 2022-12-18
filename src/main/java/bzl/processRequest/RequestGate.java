package bzl.processRequest;

import entity.Request;
import entity.RequestDeserializer;
import exceptions.RepoException;
import jdk.swing.interop.SwingInterOpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repo.GSONRepo;

import java.util.*;
import java.util.concurrent.*;

public class RequestGate {

    private ConcurrentLinkedQueue<Request> requestQ;
    private Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    private ExecutorService executorService;

    private static final Logger log = LogManager.getLogger("Main");

    public RequestGate() {
        requestQ = new ConcurrentLinkedQueue<Request>();
        processingMap = new HashMap<Long, Boolean>();
        try {
            repo = GSONRepo.getInstance();
        }
        catch(RepoException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(10);
    }

    public void addRequest(String req)
    {
        System.out.println("Adaug request");
        var request = RequestDeserializer.toRequest(req);
        requestQ.add(request);
    }

    public void sendRequestsForProcessing() {

        List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
        CompletionService<Boolean> completionService =
                new ExecutorCompletionService<Boolean>(executorService);

        int wtf = 0;

        for (Request r : requestQ) {
            if (!processingMap.containsKey(r.getCompanyId())) {
                System.out.println("Following request has been sent for processing: ");
                System.out.println(r);
                log.info("Following request has been sent for processing: ");
                log.info(r);
                futureList.add(completionService.submit(new ProcessRequest(r, repo)));
            }
        }

        executorService.shutdown();

        int received = 0;
        while(received < futureList.size()) {
            try {
                System.out.println("Request has been processed");
                log.info("Request has been processed");
                Future<Boolean> resultFuture = completionService.take();
                Boolean result = resultFuture.get();
                received ++;
                futureList.remove(resultFuture);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
}
