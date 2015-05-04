package com.devbliss.gpullr.util.http;

import org.springframework.stereotype.Component;

/**
 * Injectable class that create new instances of {@link ValuePairList}s
 */
@Component
public class ValuePairListFactory {

  public ValuePairList getNewValuePairList(int initialCapacity) {
    return ValuePairList.create(initialCapacity);
  }

}


