package rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

public class ChannelsPool extends ObjectPool<Channel> {

    private Connection connection;

    public ChannelsPool(Connection connection) {
        this.connection = connection;
    }

    @Override
    public synchronized Channel create() {
        try {
            var channel = connection.createChannel();
            channel.basicQos(100);
            return channel;
        }
        catch( IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
