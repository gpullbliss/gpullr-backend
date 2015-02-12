package com.devbliss.gpullr.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.BaseTest;
import com.devbliss.gpullr.controller.dto.UserConverter;
import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.service.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for {@link com.devbliss.gpullr.controller.UserController}.
 */
public class UserControllerTest extends BaseTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  @Mock
  private UserConverter userConverter;

  @Mock
  private UserDto userDto;

  @Test
  public void saveCallsUserService() {
    userController.create(userDto);
    verify(userService).save(any(User.class));
  }

}
