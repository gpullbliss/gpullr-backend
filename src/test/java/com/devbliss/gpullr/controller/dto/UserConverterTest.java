package com.devbliss.gpullr.controller.dto;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.BaseTest;
import com.devbliss.gpullr.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Unit tests for {@link UserConverter}
 *
 */
public class UserConverterTest extends BaseTest {

  @InjectMocks
  private UserConverter userConverter;

  private User user;

  @Before
  public void setup() {
    super.setUp();

    user = new User();
    user.id = 1;
    user.avatarUrl = "someAvatarUrl";
    user.username = "testUser";
    user.fullName = "Test User";
  }

  @Test
  public void toDto() {
    UserDto dto = userConverter.toDto(user);

    assertEquals(user.id, Integer.valueOf(dto.id));
    assertEquals(user.avatarUrl, dto.avatarUrl);
    assertEquals(user.username, dto.username);
    assertEquals(user.fullName, dto.fullName);
  }
}
