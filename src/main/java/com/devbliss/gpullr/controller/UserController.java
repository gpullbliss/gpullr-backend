package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.session.UserSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  private UserSession userSession;

  @RequestMapping(method = RequestMethod.GET)
  public List<User> getAllUsers() {
    return userService.findAll();
  }

  @RequestMapping(value = "/login/{id}", method = RequestMethod.POST)
  public User getNothing(@PathVariable("id") int id) {
    System.out.println("########### Da sindwa drin! " + id);
    // find user in db and set for userSession
    User loggedInUser = userService.findById(id);
    userSession.user = loggedInUser;
    return loggedInUser;
  }
}
