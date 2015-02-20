package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.UserConverter;
import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage users.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserConverter userConverter;

  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> getAllOrgaMembers() {
    List<UserDto> result = new ArrayList();
    userService.findAllOrgaMembers().forEach(u -> result.add(userConverter.toDto(u)));
    return result;
  }

  @RequestMapping(value = "/login/{id}",
                  method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public void login(@PathVariable("id") int id) {
    userService.login(id);
  }

  @RequestMapping(value = "/me",
                  method = RequestMethod.GET)
  public UserDto whoAmI() {
    User entity = userService.whoAmI();
    return userConverter.toDto(entity);
  }

}
