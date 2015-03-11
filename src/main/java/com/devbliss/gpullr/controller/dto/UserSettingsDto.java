package com.devbliss.gpullr.controller.dto;

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

}
