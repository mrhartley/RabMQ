package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security;

import java.io.File;

// Provides the Keystore configuration information - file and password

public class TrustStoreConfig {
  private File store;
  private char[] password;

  public TrustStoreConfig(File store, String password) {
    this.store = store;
    this.password = password.toCharArray();
  }

  public File getStore() {
    return store;
  }

  public char[] getPassword() {
    return password;
  }
}
