package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils;

import java.io.File;
import java.util.Map;

import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.TLSConfig;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.KeyStoreConfig;
import com.appiancorp.cs.plugin.rabbitMQServices.smartservice.security.TrustStoreConfig;
import com.appiancorp.suiteapi.security.external.SecureCredentialsStore;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.type.TypeService;
import com.appiancorp.ps.plugins.typetransformer.*;
import static com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.UtilConstants.*;

import static java.lang.Long.parseLong;

public class AppianUtil {

  public AppianUtil() {

  }

  public static TLSConfig getSecurityConfig(SecureCredentialsStore scs, ContentService cs, TypeService ts, String scsKeyStoreKey)
    throws Exception {

    String tlsProtocol, host, truststorePassword, keystorePassword;
    File keystore = null, truststore = null;
    KeyStoreConfig ksc = null;
    TrustStoreConfig tsc = null;
    AppianTypeFactory tf = AppianTypeFactory.newInstance(ts);

    Map<String, String> secStoreCreds = scs.getSystemSecuredValues(scsKeyStoreKey);

    // Get Host
    if (!secStoreCreds.containsKey(UtilConstants.DEFAULT_VIRTUAL_HOST_KEY)) {
      throw new RuntimeException(
        String.format("Required field %s does not exist in Secure Credential Store (%s)", DEFAULT_VIRTUAL_HOST_KEY,
          scsKeyStoreKey));
    } else {
      host = secStoreCreds.get(DEFAULT_VIRTUAL_HOST_KEY);
    }

    // Get Keystore Passphrase
    if (!secStoreCreds.containsKey(UtilConstants.JKS_PASSWORD_KEY)) {
      throw new RuntimeException(
        String.format("Required field %s does not exist in Secure Credential Store (%s)", UtilConstants.JKS_PASSWORD_KEY,
          scsKeyStoreKey));
    } else {
      keystorePassword = secStoreCreds.get(UtilConstants.JKS_PASSWORD_KEY);
    }
    // Get Java Key Store
    if (!secStoreCreds.containsKey(UtilConstants.JKS_KEY)) {
      throw new RuntimeException(
        String.format("Required field %s does not exist in Secure Credential Store (%s)", UtilConstants.JKS_KEY,
          scsKeyStoreKey));
    } else {
      keystore = new File(cs.getInternalFilename(parseLong(secStoreCreds.get(UtilConstants.JKS_KEY))));
    }

    // Get Truststore password
    if (!secStoreCreds.containsKey(UtilConstants.TRUSTSTORE_PASSWORD_KEY)) {
      throw new RuntimeException(
        String.format("Required field %s does not exist in Secure Credential Store (%s)", UtilConstants.TRUSTSTORE_PASSWORD_KEY,
          scsKeyStoreKey));
    } else {
      truststorePassword = secStoreCreds.get(UtilConstants.TRUSTSTORE_PASSWORD_KEY);
    }

    // Get Truststore Document
    if (!secStoreCreds.containsKey(UtilConstants.TRUSTSTORE_KEY)) {
      throw new RuntimeException(
        String.format("Required field %s does not exist in Secure Credential Store (%s)", UtilConstants.TRUSTSTORE_KEY,
          scsKeyStoreKey));
    } else {
      truststore = new File(cs.getInternalFilename(parseLong(secStoreCreds.get(UtilConstants.TRUSTSTORE_KEY))));
    }

    if (!secStoreCreds.containsKey(UtilConstants.TLS_PROTOCOL)) {
      tlsProtocol = DEFAULT_TLS_PROTOCOL;
    } else {
      tlsProtocol = secStoreCreds.get(UtilConstants.TLS_PROTOCOL);
    }

    if (keystore != null && keystorePassword != null) {
      ksc = new KeyStoreConfig(keystore, keystorePassword);
    } else if (keystore != null || keystorePassword != null) {
      throw new RuntimeException("Both keystore properties must be supplied");
    }

    if (keystore != null && keystorePassword != null && truststore != null && truststorePassword != null) {
      ksc = new KeyStoreConfig(keystore, keystorePassword);
      tsc = new TrustStoreConfig(truststore, truststorePassword);
    } else if (keystore == null || keystorePassword == null) {
      throw new RuntimeException("Both keystore properties must be supplied");
    } else if (truststore == null || truststorePassword == null) {
      throw new RuntimeException("Both truststore properties must be supplied");
    }

    return new TLSConfig(ksc, tsc, tlsProtocol, host);

  }
}
