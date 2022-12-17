package bzl.processRequest;

import entity.Request;
import entity.RequestDeserializer;
import entity.RequestResponse;
import exceptions.RepoException;
import jdk.swing.interop.SwingInterOpUtils;
import repo.GSONRepo;

import java.util.*;
import java.util.concurrent.*;

public class RequestGate {

    private ConcurrentLinkedQueue<Request> requestQ;
    private Map<Long, Boolean> processingMap;
    private GSONRepo repo;
    private ExecutorService executorService;
    private volatile boolean start;

    public RequestGate(ExecutorService executor) {
        requestQ = new ConcurrentLinkedQueue<Request>();
        processingMap = new HashMap<Long, Boolean>();
        try {
            repo = GSONRepo.getInstance();
        }
        catch(RepoException e) {
            e.printStackTrace();
        }
        executorService = executor;
        this.start = false;
    }

    public void addRequest(String req)
    {
        var request = RequestDeserializer.toRequest(req);
        requestQ.add(request);
    }

    public void start()
    {
        if(true)
        {
            this.start = true;
            sendRequestsForProcessing();
        }
    }

    void sendRequestsForProcessing()
    {
        List<Future<RequestResponse>> futureList = new ArrayList<Future<RequestResponse>>();
        while (start) {
            Thread.onSpinWait();

            for(Request r : requestQ)
            {
                if (!processingMap.containsKey(r.getCompanyId()))
                {
                    System.out.println("Following request has been sent for processing: ");
                    System.out.println(r);
                    futureList.add(executorService.submit(new ProcessRequest(r, repo)));
                }
            }

            for(Future<RequestResponse> f: futureList)
            {
                if(f.isDone()) {
                    System.out.println("Request has been processed!");
                    try {
                        System.out.println(f.get());
                    }
                    catch (InterruptedException | ExecutionException i)
                    {
                        i.printStackTrace();
                    }
                    futureList.remove(f);
                }
            }
            System.out.println(start);
        }
    }
}
