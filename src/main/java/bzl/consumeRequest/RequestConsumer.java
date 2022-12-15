package bzl.consumeRequest;

import bzl.processRequest.RequestGate;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class RequestConsumer extends DefaultConsumer {
    RequestGate requestGate;
    ExecutorService executor;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public RequestConsumer(Channel channel, String rabbitQ, RequestGate reqGate) {
        super(channel);
        requestGate = reqGate;
        try {
            channel.basicConsume(rabbitQ, true, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)

    {
        requestGate.addRequest(new String(body, StandardCharsets.UTF_8));
        requestGate.start();
    }
}


// consumeRequest --> get messsage from rabbitMQ
//                --> format the message and send the useful information to processRequest
