package bzl.consumeRequest;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class RequestConsumer extends DefaultConsumer {
    String request;
    ExecutorService executor;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public RequestConsumer(Channel channel, String rabbitQ, ExecutorService executor) {
        super(channel);
        this.executor = executor;
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
        Future<Boolean> future = executor.submit(new ConsumeT(new String(body, StandardCharsets.UTF_8)));
        try {
            Boolean resp = future.isDone() ? future.get() : null;
            if (resp != null)
            {
                String msg = future.get() ? "Wtf works" : "Wtf doesn't wokr";
                System.out.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


// consumeRequest --> get messsage from rabbitMQ
//                --> format the message and send the useful information to processRequest
