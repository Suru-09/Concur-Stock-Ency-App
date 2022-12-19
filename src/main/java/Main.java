import bzl.consumer.RequestConsumer;
import bzl.consumers.ResponseConsumer;
import bzl.processRequest.RequestGate;
import bzl.event.RabbitEventT;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Client;
import entity.Company;
import entity.Request;
import entity.RequestGenerator;
import exceptions.RepoException;
import org.apache.logging.log4j.LogManager;

import rabbitMQ.ChannelsPool;
import repo.ClientsRepo;
import repo.GSONRepo;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import org.apache.logging.log4j.Logger;


public class Main {

    static Logger log = LogManager.getLogger(Main.class.getName());
    private static final String sendRequestRabbitQ = "Concurr";
    private static final String clientRabbitQ = "Client";


    public static void main(String[] argv) throws IOException, TimeoutException, RepoException
    {
        GSONRepo repo = GSONRepo.getInstance();
        ClientsRepo clientRepo = ClientsRepo.getInstance();

        try {
            repo.loadData();
            clientRepo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        Connection sendRequestConnection = new ConnectionFactory().newConnection();
        ChannelsPool channelsPool = new ChannelsPool(sendRequestConnection);
        RequestGate reqGate = RequestGate.getInstance(channelsPool);

        RequestGenerator reqGenerator = new RequestGenerator(repo.getAll().toArray(new Company[0]), clientRepo.getAll().toArray(new Client[0]));
        List<Request> myReqList = reqGenerator.RequestPullGenerator(20);

        log.error("Size of my requests list:");
        log.error(myReqList.size());


        ExecutorService sendRequestThread = Executors.newCachedThreadPool();
        for(Request someReq: myReqList)
        {
            RabbitEventT work = new RabbitEventT(someReq.toString(), sendRequestRabbitQ, channelsPool);
            sendRequestThread.submit(work);
        }

        int len = Math.max((myReqList.size() / 10), 100);
        for(int i = 0; i < len; ++i)
        {
            RequestConsumer reqConsumer = new RequestConsumer(channelsPool, sendRequestRabbitQ, reqGate);
            ResponseConsumer responseConsumer = new ResponseConsumer(channelsPool.acquire(), clientRabbitQ);
        }

        ScheduledExecutorService schedExec = Executors.newSingleThreadScheduledExecutor();
        schedExec.scheduleWithFixedDelay(
                new Runnable()
                {
                    public void run()
                    {
                        System.out.println("ScheduledExecutor starts");
                        reqGate.sendRequestsForProcessing();
                    }
                }, 500, 50, TimeUnit.MILLISECONDS);
    }
}