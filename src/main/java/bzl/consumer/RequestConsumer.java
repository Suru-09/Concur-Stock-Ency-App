package bzl.consumer;


import bzl.processRequest.RequestGate;
import com.rabbitmq.client.*;
import rabbitMQ.ChannelsPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class RequestConsumer extends DefaultConsumer {
    RequestGate requestGate;
    ExecutorService executor;
    ChannelsPool channelsPool;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channelsPool the ChannelsPool from which we acquire channel
     */
    public RequestConsumer(ChannelsPool channelsPool, String rabbitQ, RequestGate reqGate) {
        super(channelsPool.acquire());
        requestGate = reqGate;
        try {
            var channel = super.getChannel();
            channel.basicConsume(rabbitQ, false, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        try {
            requestGate.addRequest(new String(body, StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var channel = super.getChannel();
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}


// consumeRequest --> get messsage from rabbitMQ
//                --> format the message and send the useful information to processRequest
