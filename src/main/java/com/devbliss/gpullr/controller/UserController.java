package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.UserConverter;
import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.GithubOAuthService;
import com.devbliss.gpullr.service.UserService;
import com.devbliss.gpullr.service.dto.GithubOAuthAccessTokenDto;
import com.devbliss.gpullr.service.dto.GithubUserDto;
import java.io.IOException;
import javax.validation.constraints.NotNull;
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

  @Autowired
  private GithubOAuthService githubOAuthService;

  @RequestMapping(value = "/oauth/github/{code}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public void authenticateOAuthRequest(@PathVariable("code") @NotNull String code) throws IOException {
    final GithubOAuthAccessTokenDto oAuthAccessToken = githubOAuthService.getAccessToken(code);
    final GithubUserDto githubUserDto = githubOAuthService.getUserByAccessToken(oAuthAccessToken);

    userService.login(githubUserDto.id);

    updateUserAccessToken(oAuthAccessToken);
  }

  @RequestMapping(
      value = "/me",
      method = RequestMethod.GET)
  public UserDto whoAmI() {
    User entity = userService.whoAmI();
    return userConverter.toDto(entity);
  }

  private void updateUserAccessToken(GithubOAuthAccessTokenDto oAuthAccessToken) {
    final User currentUser = userService.getCurrentUserIfLoggedIn().get();
    currentUser.accessToken = oAuthAccessToken.access_token;
    userService.insertOrUpdate(currentUser);
  }

}
