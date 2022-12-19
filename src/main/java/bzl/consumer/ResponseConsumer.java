package bzl.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseConsumer extends DefaultConsumer {
    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */

    private static final Logger log = LogManager.getLogger("Main");

    public ResponseConsumer(Channel channel, String rabbitQ) {
        super(channel);
        try {
            channel.basicConsume(rabbitQ, true, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        super.handleDelivery(consumerTag, envelope, properties, body);
        System.out.println("Client response: " + new String(body, StandardCharsets.UTF_8));
        log.info("Client response: " + new String(body, StandardCharsets.UTF_8));
    }
}
