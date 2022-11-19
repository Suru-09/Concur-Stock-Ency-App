
import com.rabbitmq.client.DeliverCallback;
import exceptions.RepoException;
import repo.GSONRepo;

// RabbitMQ
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] argv) throws IOException, TimeoutException {
        GSONRepo repo = new GSONRepo();
        try {
            repo.loadData();
        } catch (RepoException ex) {
            ex.printStackTrace();
        }

//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//            String QUEUE_NAME = "Concurr";
//            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//            String message = "PLM SENT World!";
//            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
//            System.out.println(" [x] Sent '" + message + "'");
//        } catch(Exception e) {
//
//        }

        String QUEUE_NAME = "Concurr";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });


    }
}

/*
    (Simulated) Multiple Client Requests(Events) ------------> processed by RabbitMQ ----------> GSONRepo
          [A class simulating clients which send
            requests concurrenctly]
 */