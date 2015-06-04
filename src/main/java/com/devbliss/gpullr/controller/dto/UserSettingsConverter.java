package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.domain.UserSettings.OrderOption;
import com.devbliss.gpullr.exception.BadRequestException;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.UserSettings} and
 * {@link com.devbliss.gpullr.controller.dto.UserSettingsDto} objects.
 */
@Component
public class UserSettingsConverter {

  public UserSettingsDto toDto(UserSettings entity) {
    UserSettingsDto dto = new UserSettingsDto();
    dto.id = entity.id;
    dto.repoBlackList = entity.repoBlackList;
    dto.language = entity.language;
    dto.desktopNotification = entity.desktopNotification;

    if (entity.assignedPullRequestsOrdering != null) {
      dto.assignedPullRequestsOrdering = toOrderOptionDto(entity.assignedPullRequestsOrdering);
    }

    if (entity.unassignedPullRequestsOrdering != null) {
      dto.unassignedPullRequestsOrdering = toOrderOptionDto(entity.unassignedPullRequestsOrdering);
    }

    return dto;
  }

  public UserSettings toEntity(UserSettingsDto dto) {
    UserSettings entity = new UserSettings();
    entity.id = dto.id;
    entity.repoBlackList = dto.repoBlackList;
    entity.language = dto.language;
    entity.desktopNotification = dto.desktopNotification;

    if (dto.assignedPullRequestsOrdering != null) {
      entity.assignedPullRequestsOrdering = toOrderOption(dto.assignedPullRequestsOrdering);
    }

    if (dto.unassignedPullRequestsOrdering != null) {
      entity.unassignedPullRequestsOrdering = toOrderOption(dto.unassignedPullRequestsOrdering);
    }

    return entity;
  }

  private OrderOption toOrderOption(UserSettingsDto.OrderOptionDto orderOption) {

    try {
      return UserSettings.OrderOption.valueOf(orderOption.name());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(String.format("Error converting OrderOptionsDto with value '%s' to OrderOptions",
          orderOption));
    }
  }

  private UserSettingsDto.OrderOptionDto toOrderOptionDto(OrderOption orderOption) {

    try {
      return UserSettingsDto.OrderOptionDto.valueOf(orderOption.name());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(String.format("Error converting OrderOptions with value '%s' to OrderOptionsDto",
          orderOption));
    }
  }

}
