package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage users.
 */
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @RequestMapping(method = RequestMethod.POST)
  public void persistUserToken(@RequestBody UserDto userDto) {
    userService.save(userDto);
  }

}
