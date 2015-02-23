package com.devbliss.gpullr.controller.dto;

import java.util.List;

/**
 * Generic DTO to wrap a list of things for JSON de-/serialization to avoid a JSON list on top level of the payload
 * which is discouraged for security reason (especially InternetExplorer).
 *
 * @author Philipp Karstedt <philipp.karstedt@devbliss.com>
 *
 * @param <T> type of things wrapped in the list
 */
public class ListDto<T> {

  private List<T> items;

  private int count;

  public ListDto() {

  }

  public ListDto(List<T> items) {
    this.items = items;
    this.count = items.size();
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
    this.count = items.size();
  }

  public int getCount() {
    return this.count;
  }
}
