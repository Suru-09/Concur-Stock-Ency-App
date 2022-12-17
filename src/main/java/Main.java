
import bzl.consumer.RequestConsumer;
import bzl.consumer.ResponseConsumer;
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
        Connection connection = new ConnectionFactory().newConnection();

        for(Request someReq: myReqList)
        {
            SendRequestT work = new SendRequestT(someReq.toString(), "Concurr");
            threadExec.submit(work);
        }

        //Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        RequestGate reqGate = new RequestGate(threadExec);
        RequestConsumer reqConsumer = new RequestConsumer(channel, "Concurr", reqGate);

        Connection connection2 = new ConnectionFactory().newConnection();
        Channel channel2 = connection2.createChannel();
        ResponseConsumer respConsumer = new ResponseConsumer(channel2, "Client");

//        threadExec.shutdown();
//        while(!threadExec.isTerminated()) {}
    }
}