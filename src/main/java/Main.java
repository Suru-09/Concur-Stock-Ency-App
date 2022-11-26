
import bzl.consumeRequest.RequestConsumer;
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
    public static void main(String[] argv) throws IOException, TimeoutException {
        GSONRepo repo = new GSONRepo();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        ExecutorService threadExec = Executors.newFixedThreadPool(4);

        List<Request> myReqList = new ArrayList<>();
        Request req = new Request(15, "Amazon", Request.RequestType.BUY);
        Request req1 = new Request(15, "Google", Request.RequestType.SELL);
        Request req2 = new Request(15, "3Pillar", Request.RequestType.SELL);
        Request req3 = new Request(15, "Apple", Request.RequestType.BUY);
        Request req4 = new Request(15, "Samsung", Request.RequestType.BUY);
        Request req5 = new Request(15, "3Pillar", Request.RequestType.SELL);
        Request req6 = new Request(15, "Google", Request.RequestType.BUY);
        Request req7 = new Request(15, "Tesla", Request.RequestType.SELL);
        Request req8 = new Request(15, "Xiaomi", Request.RequestType.SELL);
        Request req9 = new Request(15, "Softnrg", Request.RequestType.BUY);

        myReqList.add(req);
        myReqList.add(req2);
        myReqList.add(req3);
        myReqList.add(req4);
        myReqList.add(req5);
        myReqList.add(req6);
        myReqList.add(req7);
        myReqList.add(req8);
        myReqList.add(req9);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);
        myReqList.add(req);


        for(Request someReq: myReqList)
        {
            SendRequestT work = new SendRequestT(someReq, "Concurr");
            threadExec.submit(work);
        }

        Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        RequestConsumer reqConsumer = new RequestConsumer(channel, "Concurr", threadExec);



    }
}