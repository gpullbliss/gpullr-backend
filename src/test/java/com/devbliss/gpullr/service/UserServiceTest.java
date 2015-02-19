package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.session.UserSession;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionSystemException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class UserServiceTest {

  private static final Integer ID = 1981;

  private static final String AVATAR_URL = "http://jira.de";

  private static final String USERNAME = "antonaustirol";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserSession userSession;

  private UserService userService;

  @Before
  public void setup() {
    userService = new UserService(userRepository, userSession);
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

  @Test
  public void findAllOrgaMembers() {
    userService.insertOrUpdate(new User(ID, USERNAME, AVATAR_URL));
    User user = userRepository.findOne(ID);
    // ensure that user is no organization member
    assertFalse(user.canLogin);

    List<User> orgaMembers = userService.findAllOrgaMembers();
    orgaMembers.forEach(mem -> assertTrue(mem.canLogin));
    orgaMembers.forEach(mem -> assertFalse(mem.id == ID));
    orgaMembers.forEach(mem -> assertFalse(mem.username == USERNAME));
  }

  // @Test
  // public void login() {
  // userService.login(ID);
  // assertNotNull(userSession.getUser());
  // }

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
