package com.appiancorp.cs.plugin.rabbitMQServices.smartservice;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp.AMQPClient;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp.ConsumeResult;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.amqp.MQConnectionBuilder;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.TLSConfig;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.AppianUtil;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.DatabaseMessageProcessor;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.UtilConstants;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.TimeUtils;
import com.appiancorp.suiteapi.common.Name;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.appiancorp.suiteapi.process.framework.AppianSmartService;
import com.appiancorp.suiteapi.process.framework.Input;
import com.appiancorp.suiteapi.process.framework.Order;
import com.appiancorp.suiteapi.process.framework.Required;
import com.appiancorp.suiteapi.process.palette.PaletteInfo;
import com.appiancorp.suiteapi.security.external.SecureCredentialsStore;
import com.appiancorp.suiteapi.type.TypeService;

import com.rabbitmq.client.Channel;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.TimeUtils.getMilliseconds;
import static com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.UtilConstants.*;

@PaletteInfo(paletteCategory = "MQ Connectors", palette = "RabbitMQ")
@Order({
  "SCSExternalSystemKey", "QueueName", "JndiName", "TableName", "MessageLimit", "ReQueue", "TimeLimitMins",
  "FatalErrorOccurred", "FatalErrorMessage", "MessageCount", "MessageErrorCount"
})

public class RabbitConsumer extends AppianSmartService {

  private static final Logger LOG = Logger.getLogger(RabbitConsumer.class);

  private final ContentService cs;
  private SecureCredentialsStore scs;
  private final TypeService ts;
  private final Context ctx;
  private static DataSource ds;
  private static String host;

  // inputs
  private String scsExternalSystemKey; // Holds the name of the truststore & jks documents and the passphrases
  private String queueName;
  private long timeLimitMins;
  private long queueLimit;
  private boolean requeue;
  private String jndiName;
  private String stagingTableName;

  // outputs
  private long messageErrorCount;
  private long messageCount;
  private static boolean fatalErrorOccurred;
  private static String fatalErrorMessage;

  public RabbitConsumer(SecureCredentialsStore scs, TypeService ts, ContentService cs, Context ctx) {

    super();
    this.scs = scs;
    this.ts = ts;
    this.cs = cs;
    this.ctx = ctx;

  }

  private void prepareDatasource() {

    // Find the datasource
    try {
      ds = (DataSource) ctx.lookup(jndiName);
      LOG.info(String.format("Found datasource %s", jndiName));
    } catch (Exception e) {
      LOG.error(String.format("%s - %s", ERROR_MESSAGE_UNABLE_TO_CONNECT_TO_DATASOURCE, jndiName), e);
      fatalErrorOccurred = true;
      fatalErrorMessage = String.format("%s - %s", ERROR_MESSAGE_UNABLE_TO_CONNECT_TO_DATASOURCE, jndiName);
      return;
    }

  }

  private void consumeMQ() throws Exception {

    TLSConfig config = AppianUtil.getSecurityConfig(scs, cs, ts, scsExternalSystemKey);
    Long timeLimit = getMilliseconds(timeLimitMins);

    Channel channel = null;

    java.sql.Connection dbConnection = null;
    DatabaseMessageProcessor dbProcessor = null;

    try {
      channel = MQConnectionBuilder.newQueueConnection(config);
      AMQPClient client = new AMQPClient(channel.getConnection());

      dbConnection = ds.getConnection();
      dbProcessor = new DatabaseMessageProcessor(dbConnection, stagingTableName);

      ConsumeResult result = client.consumeQueue(queueName, dbProcessor, timeLimit, queueLimit, requeue);

      messageCount = result.getMessageCount();
      messageErrorCount = result.getMessageErrorCount();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (Exception e) {
      LOG.error(String.format("Error reading from queue. %s, Cause: %s", e.getMessage(), e.getCause(), e));
      fatalErrorOccurred = true;
      fatalErrorMessage = ExceptionUtils.getRootCauseMessage(e);
    } finally {
      try {
        if (channel.isOpen()) {
          channel.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
      try {
        if (channel.getConnection().isOpen()) {
          channel.getConnection().close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        dbProcessor.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (dbConnection.isClosed()) {

        } else {
          dbConnection.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public void run() throws SmartServiceException {

    // validations
    if (!TimeUtils.isTimeValid(timeLimitMins)) {
      this.fatalErrorOccurred = true;
      this.fatalErrorMessage = UtilConstants.ERROR_MESSAGE_TIME_RANGE;
    }
    ;

    prepareDatasource();

    try {
      consumeMQ();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Input(required = Required.ALWAYS)
  @Name("SCSExternalSystemKey")
  public void setScsExternalSystemKey(String val) {
    this.scsExternalSystemKey = val;
  }

  @Input(required = Required.ALWAYS)
  @Name("QueueName")
  public void setQueueName(String val) {
    this.queueName = val;
  }

  @Input(required = Required.ALWAYS)
  @Name("JndiName")
  public void setJndiName(String val) {
    this.jndiName = val;
  }

  @Input(required = Required.ALWAYS)
  @Name("TableName")
  public void setTableName(String val) {
    this.stagingTableName = val;
  }

  @Input(required = Required.OPTIONAL, defaultValue = "200")
  @Name("MessageLimit")
  public void setQueueLimit(Long val) {
    this.queueLimit = val;
  }

  @Input(required = Required.OPTIONAL, defaultValue = "false")
  @Name("ReQueue")
  public void setRequeue(Boolean val) {
    this.requeue = val;
  }

  @Input(required = Required.OPTIONAL, defaultValue = "5")
  @Name("TimeLimitMins")
  public void setTimeLimit(Long val) {
    this.timeLimitMins = val;
  }

  @Name("FatalErrorOccurred")
  public boolean getFatalErrorOccurred() {
    return fatalErrorOccurred;
  }

  @Name("FatalErrorMessage")
  public String getFatalErrorMessage() {
    return fatalErrorMessage;
  }

  @Name("MessageCount")
  public Long getMessageCount() {
    return messageCount;
  }

  @Name("MessageErrorCount")
  public Long getMessageErrorCount() {
    return messageErrorCount;
  }
}
