package com.devbliss.gpullr.controller.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for JSON de-/serialization of {@link com.devbliss.gpullr.domain.UserSettings} instances.
 */
public class UserSettingsDto {

  public enum OrderOptionDto {
    ASC,
    DESC
  }

  public long id;

  public OrderOptionDto orderOptionDto;

  public List<Integer> repoBlackList = new ArrayList<>();

  public String language;

}
