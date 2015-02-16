package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage users.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;
  
  @RequestMapping(method=RequestMethod.GET)
  public List<User> getAllUsers() {
    return userService.findAll();
  }

}
