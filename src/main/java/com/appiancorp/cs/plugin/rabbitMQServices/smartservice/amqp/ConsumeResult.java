package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp;

public class ConsumeResult {
  private long messageCount;
  private long messageErrorCount;

  public ConsumeResult(long messageCount, long messageErrorCount) {
    this.messageCount = messageCount;
    this.messageErrorCount = messageErrorCount;
  }

  public long getMessageCount() {
    return messageCount;
  }

  public long getMessageErrorCount() {
    return messageErrorCount;
  }
}
