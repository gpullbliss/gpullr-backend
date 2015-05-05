package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.UserConverter;
import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.GithubOauthService;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.dto.GithubOauthAccessToken;
import com.devbliss.gpullr.service.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

  @Autowired
  private GithubOauthService githubOauthService;

  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> getAllOrgaMembers() {
    return userService
        .findAllOrgaMembers()
        .stream()
        .map(userConverter::toDto)
        .collect(Collectors.toList());
  }

  @RequestMapping(value = "/login/{id}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @Deprecated
  public void login(@PathVariable("id") int id) {
    userService.login(id);
  }

  @RequestMapping(value = "/oauth/callback", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.CREATED)
  public void oauthCallback(@RequestParam("code") String code) throws IOException {

    final GithubOauthAccessToken oauthAccessToken = githubOauthService.getAccessToken(code);
    final GithubUser githubUser = githubOauthService.getUserByAccessToken(oauthAccessToken);

    userService.login(githubUser.id);

    updateUserAccessToken(oauthAccessToken);
  }

  @RequestMapping(
      value = "/me",
      method = RequestMethod.GET)
  public UserDto whoAmI() {
    User entity = userService.whoAmI();
    return userConverter.toDto(entity);
  }

  private void updateUserAccessToken(GithubOauthAccessToken oauthAccessToken) {
    final User currentUser = userService.getCurrentUserIfLoggedIn().get();
    currentUser.accessToken = oauthAccessToken.access_token;
    userService.insertOrUpdate(currentUser);
  }

}
