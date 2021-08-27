package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp;

import com.rabbitmq.client.SslContextFactory;

import javax.net.ssl.SSLContext;

public class TlsContextFactory implements SslContextFactory {

  public SSLContext create(String s) {
    return null;
  }
}
