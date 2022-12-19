import bzl.consumer.RequestConsumer;
import bzl.processRequest.RequestGate;
import bzl.event.RabbitEventT;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Company;
import entity.Request;
import entity.RequestGenerator;
import exceptions.RepoException;
import org.apache.logging.log4j.LogManager;

import rabbitMQ.ChannelsPool;
import repo.GSONRepo;


import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import org.apache.logging.log4j.Logger;


public class Main {

    static Logger log = LogManager.getLogger(Main.class.getName());
    private static final String sendRequestRabbitQ = "Concurr";


    public static void main(String[] argv) throws IOException, TimeoutException, RepoException
    {
        GSONRepo repo = GSONRepo.getInstance();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        Connection sendRequestConnection = new ConnectionFactory().newConnection();
        ChannelsPool channelsPool = new ChannelsPool(sendRequestConnection);
        RequestGate reqGate = RequestGate.getInstance();

        RequestGenerator reqGenerator = new RequestGenerator(repo.getAll().toArray(new Company[0]), 10);
        List<Request> myReqList = reqGenerator.RequestPullGenerator(10000);
        log.error("Size of my requests list:");
        log.error(myReqList.size());


        ExecutorService sendRequestThread = Executors.newCachedThreadPool();
        for(Request someReq: myReqList)
        {
            RabbitEventT work = new RabbitEventT(someReq.toString(), sendRequestRabbitQ, channelsPool);
            sendRequestThread.submit(work);
        }

        int len = Math.max((myReqList.size() / 100), 10);
        System.out.println("LEN IS" + len);
        for(int i = 0; i < len; ++i)
        {
            RequestConsumer reqConsumer = new RequestConsumer(channelsPool, sendRequestRabbitQ, reqGate);
        }

        ScheduledExecutorService schedExec = Executors.newSingleThreadScheduledExecutor();
        schedExec.scheduleWithFixedDelay(
                new Runnable()
                {
                    public void run()
                    {
                    System.out.println("HHHHHHHEEEEEEEEERRRRREEEEEE");
                    reqGate.sendRequestsForProcessing();
                    }
                }, 500, 50, TimeUnit.MILLISECONDS);


        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
        for (Thread t : threads) {
            System.out.printf("%-15s \t %-15s \t %-15d \t %s\n", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
        }
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) throws IOException {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.MINUTES)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}