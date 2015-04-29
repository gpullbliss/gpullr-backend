package com.devbliss.gpullr.controller.dto;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.domain.UserSettings;
import java.util.Arrays;
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
    userSettings.assignedPullRequestsOrdering = UserSettings.OrderOption.ASC;
    userSettings.unassignedPullRequestsOrdering = UserSettings.OrderOption.DESC;
    userSettings.repoBlackList.addAll(Arrays.asList(1, 2));

    userSettingsDto = new UserSettingsDto();
    userSettingsDto.id = 2;
    userSettingsDto.assignedPullRequestsOrdering = UserSettingsDto.OrderOptionDto.DESC;
    userSettingsDto.unassignedPullRequestsOrdering = UserSettingsDto.OrderOptionDto.ASC;
    userSettingsDto.repoBlackList.addAll(Arrays.asList(1, 2, 3));
  }

  @Test
  public void toDto() {
    UserSettingsDto dto = userSettingsConverter.toDto(userSettings);

    assertEquals(userSettings.id, dto.id);
    assertEquals(userSettings.assignedPullRequestsOrdering.name(), dto.assignedPullRequestsOrdering.name());
    assertEquals(userSettings.unassignedPullRequestsOrdering.name(), dto.unassignedPullRequestsOrdering.name());
    assertEquals(userSettings.repoBlackList.size(), dto.repoBlackList.size());
  }

  @Test
  public void toEntity() {
    UserSettings entity = userSettingsConverter.toEntity(userSettingsDto);

    assertEquals(userSettingsDto.id, entity.id);
    assertEquals(userSettingsDto.assignedPullRequestsOrdering.name(), entity.assignedPullRequestsOrdering.name());
    assertEquals(userSettingsDto.unassignedPullRequestsOrdering.name(), entity.unassignedPullRequestsOrdering.name());
    assertEquals(userSettingsDto.repoBlackList.size(), entity.repoBlackList.size());
  }

}
