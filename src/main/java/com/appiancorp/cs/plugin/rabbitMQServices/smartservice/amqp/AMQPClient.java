package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.DatabaseMessageProcessor;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import org.apache.log4j.Logger;

import java.io.IOException;

public class AMQPClient {
  private static final Logger LOG = Logger.getLogger(AMQPClient.class);
  private Connection qc;

  public AMQPClient(Connection qc) {
    this.qc = qc;
  }

  public ConsumeResult consumeQueue(String queue, DatabaseMessageProcessor processor, long timeLimitMs, long messageLimit, boolean requeue)
    throws IOException {
    LOG.debug(String.format("Consume queue = %s, time limit ms = %d", queue, timeLimitMs));
    if (qc.isOpen())
      ;
    {
      try (Channel channel = qc.createChannel()) {
        return consume(queue, channel, processor, timeLimitMs, messageLimit, requeue);
      } catch (Exception e) {
        LOG.error(e);
        return null;
      }
    }
  }

  private ConsumeResult consume(String queue, Channel channel, DatabaseMessageProcessor processor, long timeLimitMs, long messageLimit,
    boolean requeue) throws IOException {
    long end = System.currentTimeMillis() + timeLimitMs;
    long messageCount = 0;
    long messageErrorCount = 0;
    try {
      GetResponse response = null;
      while ((response = channel.basicGet(queue, false)) != null) {
        long deliveryTag = response.getEnvelope().getDeliveryTag();
        boolean success = processor.apply(response);
        if (success) {
          channel.basicAck(deliveryTag, false);
          messageCount++;
        } else {
          channel.basicNack(deliveryTag, false, requeue);
          messageErrorCount++;
        }
        if (System.currentTimeMillis() >= end) {
          break;
        }

        if (messageCount + messageErrorCount == messageLimit) {
          break;
        }
      }
    } finally {
    }

    return new ConsumeResult(messageCount, messageErrorCount);
  }

  public void produceQueue(String queue, String message) throws IOException {
    Channel channel = qc.createChannel();
    produce(channel, queue, message);
  }

  private void produce(Channel channel, String queue, String message) throws IOException {
    channel.queueDeclarePassive(queue);
    channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
  }
}
