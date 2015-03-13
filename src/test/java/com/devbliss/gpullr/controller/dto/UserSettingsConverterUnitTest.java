package com.devbliss.gpullr.controller.dto;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.domain.UserSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link com.devbliss.gpullr.controller.dto.UserSettingsConverter}.
 */
public class UserSettingsConverterUnitTest {

  @InjectMocks
  private UserSettingsConverter userSettingsConverter;

  private UserSettings userSettings;

  private UserSettingsDto userSettingsDto;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    userSettings = new UserSettings();
    userSettings.id = 1;
    userSettings.defaultPullRequestListOrdering = UserSettings.OrderOption.ASC;

    userSettingsDto = new UserSettingsDto();
    userSettingsDto.id = 2;
    userSettingsDto.orderOptionDto = UserSettingsDto.OrderOptionDto.DESC;
  }

  @Test
  public void toDto() {
    UserSettingsDto dto = userSettingsConverter.toDto(userSettings);

    assertEquals(userSettings.id, dto.id);
    assertEquals(userSettings.defaultPullRequestListOrdering.name(), dto.orderOptionDto.name());
  }

  @Test
  public void toEntity() {
    UserSettings entity = userSettingsConverter.toEntity(userSettingsDto);

    assertEquals(userSettingsDto.id, entity.id);
    assertEquals(userSettingsDto.orderOptionDto.name(), entity.defaultPullRequestListOrdering.name());
  }

}