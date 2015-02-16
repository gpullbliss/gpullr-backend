package com.devbliss.gpullr.service;

import org.springframework.transaction.TransactionSystemException;

import org.springframework.orm.jpa.JpaSystemException;
import static org.junit.Assert.assertEquals;
import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.UserRepository;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class UserServiceTest {

  private static final Integer ID = 1981;

  private static final String AVATAR_URL = "http://jira.de";

  private static final String USERNAME = "antonaustirol";

  @Autowired
  private UserRepository userRepository;

  private UserService userService;

  @Before
  public void setup() {
    userService = new UserService(userRepository);
  }

  @After
  public void teardown() {
    userRepository.deleteAll();
  }

  @Test
  public void insertUpdateFetchAll() {
    // verify users table is empty at the beginning:
    assertEquals(0, userService.findAll().size());

    // insert new user:
    userService.insertOrUpdate(new User(ID, USERNAME, AVATAR_URL));

    // verify successful insert:
    List<User> users = userService.findAll();
    assertEquals(1, users.size());
    User loaded = users.get(0);
    assertEquals(ID, loaded.id);
    assertEquals(AVATAR_URL, loaded.avatarUrl);
    assertEquals(USERNAME, loaded.username);

    // update user:
    final String updatedAvatarUrl = AVATAR_URL + "_/updated";
    final String updatedUsername = USERNAME + "_fromAustria";
    userService.insertOrUpdate(new User(ID, updatedUsername, updatedAvatarUrl));

    // verify update:
    users = userService.findAll();
    assertEquals(1, users.size());
    loaded = users.get(0);
    assertEquals(ID, loaded.id);
    assertEquals(updatedAvatarUrl, loaded.avatarUrl);
    assertEquals(updatedUsername, loaded.username);
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void usernameUnique() {
    // create a user:
    userService.insertOrUpdate(new User(ID, USERNAME, AVATAR_URL));

    // saving another user with same username but different id should fail:
    userService.insertOrUpdate(new User(ID + 7, USERNAME, AVATAR_URL + "_blah"));
  }

  @Test(expected = JpaSystemException.class)
  public void userNeedsId() {
    userService.insertOrUpdate(new User(null, USERNAME, AVATAR_URL));
  }

  @Test(expected = TransactionSystemException.class)
  public void userNeedsUsername() {
    userService.insertOrUpdate(new User(ID, "", AVATAR_URL));
  }
}
