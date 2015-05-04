package com.devbliss.gpullr.util.http;

import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class ValuePairListFactory {

  public ValuePairList getNewValuePairList(int initialCapacity) {
    return ValuePairList.create(initialCapacity);
  }

}


