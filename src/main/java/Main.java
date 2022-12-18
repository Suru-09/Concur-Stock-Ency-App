
import bzl.consumers.RequestConsumer;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;


public class Main {

    static Logger log = LogManager.getLogger(Main.class.getName());
    private static final String sendRequestRabbitQ = "Concurr";
    static final Connection sendRequestConnection;

    static {
        try {
            sendRequestConnection = new ConnectionFactory().newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] argv) throws IOException, TimeoutException, RepoException
    {
        GSONRepo repo = GSONRepo.getInstance();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

        RequestGenerator reqGenerator = new RequestGenerator(repo.getAll().toArray(new Company[0]), 10);
        ChannelsPool channelsPool = new ChannelsPool(sendRequestConnection);
        List<Request> myReqList = reqGenerator.RequestPullGenerator(1500);
        log.error(myReqList.size());


        ExecutorService sendRequestThread = Executors.newCachedThreadPool();
        for(Request someReq: myReqList)
        {
            RabbitEventT work = new RabbitEventT(someReq.toString(), sendRequestRabbitQ, channelsPool);
            sendRequestThread.submit(work);

            RequestGate reqGate = new RequestGate();
            RequestConsumer reqConsumer = new RequestConsumer(channelsPool, sendRequestRabbitQ, reqGate);
        }

        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
        for (Thread t : threads) {
            System.out.printf("%-15s \t %-15s \t %-15d \t %s\n", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
        }
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool, Connection connection) throws IOException {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.MINUTES)) {
                connection.close();
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            connection.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}