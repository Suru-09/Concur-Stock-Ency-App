package bzl.simulateRequest;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Request;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;


public class SendRequestT implements Runnable {

    private String messageBody;
    private String rabbitQ;

    public SendRequestT(String messageBody, String rabbitQ)
    {
        this.messageBody = messageBody;
        this.rabbitQ = rabbitQ;
    }

    @Override
    public void run()
    {
        processCommand();
        Boolean res = sendRequest(messageBody);
        processCommand();
    }

    public Boolean sendRequest(String messageBody)
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // TO DO: we SHOULD NOT have a new connection for every request
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())
        {
            channel.queueDeclare(rabbitQ, true, false, false, null);
            channel.basicPublish("", rabbitQ, null, messageBody.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + messageBody + "'");

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
            Thread.sleep(50);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
