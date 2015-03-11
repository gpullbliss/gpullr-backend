package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.UserSettings} and
 * {@link com.devbliss.gpullr.controller.dto.UserSettingsDto} objects.
 */
@Component
public class UserSettingsConverter {

  @Log
  private Logger logger;

  public UserSettingsDto toDto(UserSettings entity) {
    UserSettingsDto dto = new UserSettingsDto();
    dto.id = entity.id;

    if (entity.defaultPullRequestListOrdering != null) {
      String orderOptionValue = entity.defaultPullRequestListOrdering.name();
      try {
        dto.orderOptionDto = UserSettingsDto.OrderOptionDto.valueOf(orderOptionValue);
      } catch (IllegalArgumentException e) {
        logger.error("Error converting OrderOptions with value '%s' to OrderOptionsDto", orderOptionValue, e);
      }
    }

    return dto;
  }

  public UserSettings toEntity(UserSettingsDto dto) {
    UserSettings entity = new UserSettings();
    entity.id = dto.id;

    if (dto.orderOptionDto != null) {
      String orderOptionValue = dto.orderOptionDto.name();
      try {
        entity.defaultPullRequestListOrdering = UserSettings.OrderOption.valueOf(orderOptionValue);
      } catch (IllegalArgumentException e) {
        logger.error("Error converting OrderOptionsDto with value '%s' to OrderOptions", orderOptionValue, e);
      }
    }

    return entity;
  }

}
