package com.devbliss.gpullr.util.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class ValuePairList {

  private final List<NameValuePair> nameValuePairs;

  private ValuePairList(int initialCapacity) {
    nameValuePairs = new ArrayList<>(initialCapacity);
  }

  public static ValuePairList create(int initialCapacity) {
    return new ValuePairList(initialCapacity);
  }

  public ValuePairList add(String key, String value) {
    nameValuePairs.add(new BasicNameValuePair(key, value));
    return this;
  }

  public UrlEncodedFormEntity buildUrlEncoded() throws UnsupportedEncodingException {
    return new UrlEncodedFormEntity(nameValuePairs);
  }

}
