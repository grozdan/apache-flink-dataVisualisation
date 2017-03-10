package finki.gm.diplomska.web;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

/**
 * Created by Grozdan.Madjarov on 3/6/2017.
 */
public class TweetsConsumer extends DefaultConsumer {
  public TweetsConsumer(Channel channel) {
    super(channel);
  }

  public String handleDeliveryy(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
      throws IOException {
    String message = new String(body, "UTF-8");
    System.out.println(" [x] Received '" + message + "'");
    return message;
  }
}
