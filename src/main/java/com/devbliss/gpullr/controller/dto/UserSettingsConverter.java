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

    String orderOptionValue = entity.defaultPullRequestListOrdering.name();
    try {
      dto.orderOptionDto = UserSettingsDto.OrderOptionDto.valueOf(orderOptionValue);
    } catch (IllegalArgumentException e) {
      logger.error("Error converting OrderOptions with value '%s' to OrderOptionsDto", orderOptionValue, e);
    }

    return dto;
  }

}
