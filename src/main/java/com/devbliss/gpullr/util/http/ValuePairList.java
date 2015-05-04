package com.devbliss.gpullr.util.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a {@link List} of {@link NameValuePair}. After the list of url-encoded pairs is composed
 * it can be transformed into an {@link UrlEncodedFormEntity}.This is typically useful while sending
 * an HTTP POST request.
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
