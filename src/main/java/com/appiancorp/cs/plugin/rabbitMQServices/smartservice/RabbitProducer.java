package com.appiancorp.cs.plugin.rabbitMQServices.smartservice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import javax.naming.Context;
import javax.sql.DataSource;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp.AMQPClient;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp.MQConnectionBuilder;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.TLSConfig;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.AppianUtil;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.type.TypeService;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.Order;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.appiancorp.suiteapi.security.external.SecureCredentialsStore;

@PaletteInfo(paletteCategory = "MQ Connectors", palette = "RabbitMQ")
@Order({
  "QueueName", "ScsExternalSystemKey", "Message", "FatalErrorOccurred", "FatalErrorMessage"
})
public class RabbitProducer extends AppianSmartService {
  private static final Logger LOG = Logger.getLogger(RabbitProducer.class);

  private ContentService cs;
  private SecureCredentialsStore scs;
  private final TypeService ts;
  private Context ctx;
  private static DataSource ds;
  private static String host;

  // inputs
  private String scsExternalSystemKey;
  private String queueName;
  private String message;
  // outputs
  private boolean FatalErrorOccurred;
  private String FatalErrorMessage;

  public RabbitProducer(SecureCredentialsStore scs, TypeService ts) {
    super();
    this.scs = scs;
    this.ts = ts;
  }

  @Override
  public void run() throws SmartServiceException {

    TLSConfig config = null;
    try {
      config = AppianUtil.getSecurityConfig(scs, cs, ts, scsExternalSystemKey);
    } catch (Exception e) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error reading the configuration. JAXB Exception");
    }

    String username = null;
    String password = null;
    String virtualHost = null;
    boolean tlsEnabled = false;
    String keyStorePP = null;
    String keyStorePath = null;
    String trustStorePP = null;
    String trustStorePath = null;

    Channel channel = null;
    try {
      channel = MQConnectionBuilder.newQueueConnection(config);
      AMQPClient client = new AMQPClient(channel.getConnection());

      if (StringUtils.isNotEmpty(queueName)) {
        try {
          client.produceQueue(queueName, message);
        } catch (Exception e) {
          FatalErrorOccurred = true;
          FatalErrorMessage = String.format("Error sending the message.");
        }
      }
    } catch (IOException e1) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion. IOException");
    } catch (NoSuchAlgorithmException e1) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion .NoSuchAlgorithmException");
    } catch (URISyntaxException e1) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion. URL Syntax Exception");
    } catch (TimeoutException e1) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion. TimeOut Exception");
    } catch (KeyManagementException e1) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion. Key Management Exception");
    } catch (Exception e) {
      FatalErrorOccurred = true;
      FatalErrorMessage = String.format("Error initializing the connexion.");
    } finally {

      try {
        if (channel != null) {
          if (channel.isOpen()) {
            channel.close();
            try {
              if (channel.getConnection() != null) {
                if (channel.getConnection().isOpen()) {
                  channel.getConnection().close();
                }
              } else
                return;
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else
          return;
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Input(required = Required.ALWAYS)
  @Name("Message")
  public void setMessage(String val) {
    this.message = val;
  }

  @Input(required = Required.ALWAYS)
  @Name("QueueName")
  public void setQueueName(String val) {
    this.queueName = val;
  }
}
