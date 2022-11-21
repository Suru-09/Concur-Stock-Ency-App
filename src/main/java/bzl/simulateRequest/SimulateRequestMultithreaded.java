package bzl.simulateRequest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Request;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulateRequestMultithreaded {
    private ExecutorService executor;
    private List<Request> requestsArr;
    private static String rabbitQName;
    private int noOfThreads;


    public SimulateRequestMultithreaded(int noOfThreads, String rabbitQName, List<Request> requestsArr)
    {
        this.noOfThreads = noOfThreads;
        SimulateRequestMultithreaded.rabbitQName = rabbitQName;
        this.requestsArr = requestsArr;
    }

    public void execute()
    {
        executor = Executors.newFixedThreadPool(this.noOfThreads);
        for(Request req : requestsArr)
        {
            Runnable worker = new WorkerThread(req);
            executor.execute(worker);
        }
        executor.shutdown();
        while(!executor.isTerminated()) {}
        System.out.println("All requests have been processed and threads ended work");
    }

    public static Boolean sendRequest(Request request)
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(rabbitQName, true, false, false, null);
            String message = request.toString();
            channel.basicPublish("", rabbitQName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");

        } catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
        return true;
    }


}
