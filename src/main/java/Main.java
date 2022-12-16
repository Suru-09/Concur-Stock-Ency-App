
import bzl.consumeRequest.RequestConsumer;
import bzl.processRequest.RequestGate;
import bzl.simulateRequest.SendRequestT;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Company;
import entity.Request;
import entity.RequestGenerator;
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
        RequestGenerator reqGenerator = new RequestGenerator(repo.getAll().toArray(new Company[0]), 10);

        List<Request> myReqList = reqGenerator.RequestPullGenerator(100);


        for(Request someReq: myReqList)
        {
            SendRequestT work = new SendRequestT(someReq, "Concurr");
            threadExec.submit(work);
        }

        Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        RequestGate reqGate = new RequestGate(threadExec);
        RequestConsumer reqConsumer = new RequestConsumer(channel, "Concurr", reqGate);

//        threadExec.shutdown();
//        while(!threadExec.isTerminated()) {}
    }
}