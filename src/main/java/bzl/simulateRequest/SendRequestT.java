package bzl.simulateRequest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Request;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;


public class SendRequestT implements Runnable {

    private Request request;
    private String rabbitQ;

    public SendRequestT(Request request, String rabbitQ)
    {
        this.request = request;
        this.rabbitQ = rabbitQ;
    }

    @Override
    public void run()
    {
        processCommand();
        Boolean res = sendRequest(request);
        processCommand();
    }

    public Boolean sendRequest(Request request)
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(rabbitQ, true, false, false, null);
            String message = request.toString();
            channel.basicPublish("", rabbitQ, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");

        } catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void processCommand()
    {
        try {
            Thread.sleep(350);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
