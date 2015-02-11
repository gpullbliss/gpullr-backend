package com.devbliss.gpullr.controller;

import static org.junit.Assert.*;

import com.devbliss.gpullr.BaseTest;
import com.devbliss.gpullr.controller.dto.UserConverter;
import com.devbliss.gpullr.controller.dto.UserDto;
import com.devbliss.gpullr.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Unit tests for {@link com.devbliss.gpullr.controller.UserConverterTest}.
 */
public class UserConverterTest extends BaseTest {

  @InjectMocks
  private UserConverter userConverter;

  private UserDto userDto;

  @Before
  public void setUp() {
    super.setUp();

    userDto = new UserDto();
    userDto.id = 1L;
    userDto.username = "username";
    userDto.fullname = "full name";
    userDto.externalUserId = "123blabla";
    userDto.token = "tokyToken";
    userDto.avatarUrl = "http://nosuchhost.tld/nosuchAvatar";
  }

  @Test
  public void toEntity() {
    User entity = userConverter.toEntity(userDto);

    assertEquals(userDto.id, entity.id);
    assertEquals(userDto.username, entity.username);
    assertEquals(userDto.fullname, entity.fullname);
    assertEquals(userDto.externalUserId, entity.externalUserId);
    assertEquals(userDto.token, entity.token);
    assertEquals(userDto.avatarUrl, entity.avatarUrl);
  }

}
