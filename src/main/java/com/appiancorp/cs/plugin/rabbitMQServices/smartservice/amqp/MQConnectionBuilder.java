package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.TLSConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeoutException;

public class MQConnectionBuilder {

  private MQConnectionBuilder() {
    // do not allow new instances of this class
  }

  public static Channel newQueueConnection(TLSConfig secConfig)
    throws IOException, TimeoutException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException,
    KeyStoreException, URISyntaxException {

    ConnectionFactory qcf = newQueueConnectionFactory(secConfig);
    qcf.setVirtualHost(secConfig.getHost());
    Connection connection = qcf.newConnection();
    Channel channel = connection.createChannel();

    return channel;
  }

  private static ConnectionFactory newQueueConnectionFactory(TLSConfig secConfig) throws NoSuchAlgorithmException,
    KeyManagementException, URISyntaxException, IOException, UnrecoverableKeyException, KeyStoreException, CertificateException {

    // Creating the SSLContextBuilder object
    SSLContextBuilder sslBuilder = SSLContexts.custom();

    // Loading the Keystore file
    File jksFile = secConfig.getKeyStoreConfig().getStore();
    File tsFile = secConfig.getTrustStoreConfig().getStore();
    sslBuilder = sslBuilder
      .setProtocol(secConfig.getTlsProtocol())
      .loadKeyMaterial(jksFile, secConfig.getTrustStoreConfig().getPassword(), secConfig.getKeyStoreConfig().getPassword())
      .loadTrustMaterial(tsFile, secConfig.getTrustStoreConfig().getPassword());

    // Building the SSLContext
    SSLContext sslContext = sslBuilder.build();

    ConnectionFactory factory = new ConnectionFactory();

    factory.useSslProtocol(sslContext);

    return factory;
  }
}
