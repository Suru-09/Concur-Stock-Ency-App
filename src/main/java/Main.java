
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
import org.apache.logging.log4j.LogManager;

import repo.GSONRepo;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;


public class Main {

    static Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] argv) throws IOException, TimeoutException, RepoException {

        log.error("I am doing things");

        GSONRepo repo = GSONRepo.getInstance();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        log.error("I am doing things");

        ExecutorService threadExec = Executors.newCachedThreadPool();
        RequestGenerator reqGenerator = new RequestGenerator(repo.getAll().toArray(new Company[0]), 10);

        List<Request> myReqList = reqGenerator.RequestPullGenerator(10);

        for(Request someReq: myReqList)
        {
            SendRequestT work = new SendRequestT(someReq, "Concurr");
            threadExec.submit(work);
        }

        Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        RequestGate reqGate = new RequestGate(threadExec);
        RequestConsumer reqConsumer = new RequestConsumer(channel, "Concurr", reqGate);
    }
}