package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils;

public final class UtilConstants {

  private UtilConstants() {
  };

  public static final String DEFAULT_VIRTUAL_HOST_KEY = "host";
  public static final String DEFAULT_TLS_ENABLED_KEY = "tls";

  public static final String JKS_PASSWORD_KEY = "jkspassword";
  public static final String JKS_KEY = "jksdocument";

  public static final String TRUSTSTORE_KEY = "truststoredocument";
  public static final String TRUSTSTORE_PASSWORD_KEY = "truststorepassword";

  public static final String TLS_PROTOCOL = "tlsprotocol";

  public static final String DEFAULT_USERNAME_KEY = "username";
  public static final String DEFAULT_TLS_PROTOCOL = "TLSv1.2";
  public static final String DEFAULT_USERNAME_TOKEN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  public static final String ERROR_MESSAGE_NO_SCS = "Unable to retrieve credentials from SCS";
  public static final String ERROR_MESSAGE_TIME_RANGE = "Time Limit must be set between 1 & 59 minutes";
  public static final String ERROR_MESSAGE_UNABLE_TO_RETRIEVE_JKS = "Unable to retrieve JKS file";
  public static final String ERROR_MESSAGE_UNABLE_TO_CONNECT_TO_DATASOURCE = "Unable to connect to datasource";

  public static final long LIMITS_LOWER_TIME_LIMIT = 1;
  public static final long LIMITS_UPPER_TIME_LIMIT = 59;

}
