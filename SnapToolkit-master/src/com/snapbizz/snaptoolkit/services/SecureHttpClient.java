package com.snapbizz.snaptoolkit.services;

import java.security.KeyStore;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.content.Context;

public class SecureHttpClient extends DefaultHttpClient {

  final Context context;

  public SecureHttpClient(Context context) {
    this.context = context;
  }

  @Override protected ClientConnectionManager createClientConnectionManager() {
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(
        new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    registry.register(new Scheme("https", newSslSocketFactory(), 443));
    return new SingleClientConnManager(getParams(), registry);
  }

  private SSLSocketFactory newSslSocketFactory() {
    try {
      KeyStore trusted = KeyStore.getInstance("BKS");
//      InputStream in = context.getResources().openRawResource(R.raw.snap);
//      try {
//        trusted.load(in, "".toCharArray());
//      } finally {
//        in.close();
//      }
      return new SSLSocketFactory(trusted);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
