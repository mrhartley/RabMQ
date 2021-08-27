package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils;

import com.rabbitmq.client.GetResponse;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public class DatabaseMessageProcessor implements Function<GetResponse, Boolean>, Closeable {
  private static final Logger LOG = Logger.getLogger(DatabaseMessageProcessor.class);
  private final PreparedStatement ps;

  public DatabaseMessageProcessor(Connection conn, String tableName) throws SQLException {
    ps = conn.prepareStatement("INSERT INTO " + tableName + " (message) VALUES(?)");
  }

  @Override
  public Boolean apply(GetResponse message) {

    String body = new String(message.getBody());

    try {
      ps.setString(1, body);
      ps.executeUpdate();

      return true;
    } catch (SQLException e) {
      LOG.error("Error saving message to database", e);
    }

    return false;
  }

  @Override
  public void close() throws IOException {
    try {
      ps.close();
    } catch (SQLException sqle) {
      throw new IOException("Failed to close statement", sqle);
    }
  }
}
