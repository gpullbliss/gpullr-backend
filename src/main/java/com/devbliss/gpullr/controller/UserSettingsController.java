package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.UserSettingsConverter;
import com.devbliss.gpullr.controller.dto.UserSettingsDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.util.Constants;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage user settings.
 */
@RestController
@RequestMapping("/users")
public class UserSettingsController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserSettingsConverter userSettingsConverter;

  @RequestMapping(value = "/{userId}/settings", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateSettings(
      @PathVariable("userId") int userId, @RequestBody @NotNull UserSettingsDto userSettingsDto) {

    UserSettings userSettings = userSettingsConverter.toEntity(userSettingsDto);
    User user = userService.updateUserSettings(userId, userSettings);
    userService.updateUserSession(user);
  }

  @RequestMapping(value = "/languages", method = RequestMethod.GET)
  public ListDto<String> getAvailableLanguages() {
    return new ListDto<>(Constants.ALLOWED_LANGUAGES
      .keySet()
      .stream()
      .sorted()
      .collect(Collectors.toList()));
  }
}
