package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.UserSettingsDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage user settings.
 */
@RestController
@RequestMapping("/users")
public class UserSettingsController {

  @RequestMapping(value = "/{userId}/settings", method = RequestMethod.PUT)
  public String updateSettings(@PathVariable("userId") int userId, @RequestBody UserSettingsDto userSettingsDto) {
    System.out.println("bla");

    return "bla";
  }

}
