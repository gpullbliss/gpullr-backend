package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.session.UserSession;
import com.devbliss.gpullr.util.Log;
import java.util.List;
import org.slf4j.Logger;
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

  @Log
  private Logger logger;

  @Autowired
  private UserService userService;

  @Autowired
  private UserSession userSession;

  @RequestMapping(method = RequestMethod.GET)
  public List<User> getAllOrgaMembers() {
    return userService.findAllOrgaMembers();
  }

  @RequestMapping(value = "/login/{id}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public void login(@PathVariable("id") int id) {
    // find user in db and set for userSession
    User loggedInUser = userService.findById(id);
    userSession.setUser(loggedInUser);
    logger.debug("Login for: " + loggedInUser.username);
  }

  @RequestMapping(value = "/me", method = RequestMethod.GET)
  public User whoAmI() {
    if (userSession.getUser() != null) {
      return userSession.getUser();
    } else {
      return null;
    }
  }

}
