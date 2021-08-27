package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.UtilConstants;
import org.apache.commons.lang.StringUtils;

public class TLSConfig {
  private KeyStoreConfig ksc;
  private TrustStoreConfig tsc;
  private String tlsProtocol;
  private String host;

  public TLSConfig(KeyStoreConfig ksc, TrustStoreConfig tsc, String tlsProtocol, String host) {
    this.ksc = ksc;
    this.tsc = tsc;
    this.host = host;

    if (StringUtils.isEmpty(tlsProtocol)) {
      this.tlsProtocol = UtilConstants.DEFAULT_TLS_PROTOCOL;
    } else {
      this.tlsProtocol = tlsProtocol;
    }
  }

  public KeyStoreConfig getKeyStoreConfig() {
    return ksc;
  }

  public TrustStoreConfig getTrustStoreConfig() {
    return tsc;
  }

  public String getTlsProtocol() {
    return tlsProtocol;
  }

  public String getHost() {
    return host;
  }
}
