package bzl.event;

import rabbitMQ.ChannelsPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class RabbitEventT implements Runnable {

    private String messageBody;
    private String rabbitQ;
    private ChannelsPool channelsPool;

    public RabbitEventT(String messageBody, String rabbitQ, ChannelsPool channelsPool)
    {
        this.messageBody = messageBody;
        this.rabbitQ = rabbitQ;
        this.channelsPool = channelsPool;
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
        var channel = channelsPool.acquire();
        try {
            channel.queueDeclare(rabbitQ, true, false, false, null);
            channel.basicPublish("", rabbitQ, null, messageBody.getBytes(StandardCharsets.UTF_8));
        } catch(IOException e)
        {
            e.printStackTrace();
            channelsPool.release(channel);
            return false;
        }
        System.out.println(" [x] Sent '" + messageBody + "'");
        channelsPool.release(channel);
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
