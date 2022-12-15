
import bzl.consumeRequest.RequestConsumer;
import bzl.processRequest.RequestGate;
import bzl.simulateRequest.SendRequestT;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Request;
import exceptions.RepoException;
import repo.GSONRepo;

// RabbitMQ

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] argv) throws IOException, TimeoutException, RepoException {
        GSONRepo repo = GSONRepo.getInstance();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        ExecutorService threadExec = Executors.newCachedThreadPool();

        List<Request> myReqList = new ArrayList<>();
        Request req = new Request(15, 1L, Request.RequestType.BUY, 10);
        Request req1 = new Request(15, 2L, Request.RequestType.SELL, 13);
        Request req2 = new Request(15, 1L, Request.RequestType.SELL, 2);
        Request req3 = new Request(15, 1L, Request.RequestType.BUY, 4);
        Request req4 = new Request(15, 1L, Request.RequestType.BUY, 500);
        Request req5 = new Request(15, 1L, Request.RequestType.SELL, 12);
        Request req6 = new Request(15, 2L, Request.RequestType.BUY, 14);
        Request req7 = new Request(15, 2L, Request.RequestType.SELL, 157);
        Request req8 = new Request(15, 1L, Request.RequestType.SELL, 169);
        Request req9 = new Request(15, 1L, Request.RequestType.BUY, 2132);

        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req3);
        myReqList.add(req4);
        myReqList.add(req5);
        myReqList.add(req6);
        myReqList.add(req7);
        myReqList.add(req8);
        myReqList.add(req9);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req);
        myReqList.add(req6);
        myReqList.add(req2);
        myReqList.add(req2);
        myReqList.add(req);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req2);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req6);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req2);


        for(Request someReq: myReqList)
        {
            SendRequestT work = new SendRequestT(someReq, "Concurr");
            threadExec.submit(work);
            for(Request someReq1: myReqList)
            {
                SendRequestT work1 = new SendRequestT(someReq1, "Concurr");
                threadExec.submit(work1);
            }

            Connection connection = new ConnectionFactory().newConnection();
            Channel channel = connection.createChannel();
            RequestGate reqGate = new RequestGate(threadExec);
            RequestConsumer reqConsumer = new RequestConsumer(channel, "Concurr", reqGate);
        }
        threadExec.shutdown();
        while(!threadExec.isTerminated()) {}
    }
}