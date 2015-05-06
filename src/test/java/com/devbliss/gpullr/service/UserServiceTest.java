package com.devbliss.gpullr.service;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.User;
import com.devbliss.gpullr.domain.UserSettings;
import com.devbliss.gpullr.exception.BadRequestException;
import com.devbliss.gpullr.exception.LoginRequiredException;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.repository.UserRepository;
import com.devbliss.gpullr.session.UserSession;
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

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class UserServiceTest {

  private static final int ID = 1981;

  private static final String AVATAR_URL = "http://jira.de";

  private static final String PROFILE_URL = "http://my.own.profile.example.com";

  private static final String FULL_NAME = "Anton aus Tirol";

  private static final String USERNAME = "antonaustirol";

  private static final Boolean CAN_LOGIN = true;

  @Autowired
  private UserRepository userRepository;

  private UserSession userSession;

  private UserService userService;

  @Before
  public void setup() {
    userSession = mock(UserSession.class);
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
    userService.insertOrUpdate(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));

    // verify successful insert:
    List<User> users = userService.findAll();
    assertEquals(1, users.size());
    User loaded = users.get(0);
    assertEquals(ID, loaded.id.intValue());
    assertEquals(AVATAR_URL, loaded.avatarUrl);
    assertEquals(USERNAME, loaded.username);
    assertEquals(PROFILE_URL, loaded.profileUrl);

    // update user:
    final String updatedAvatarUrl = AVATAR_URL + "_/updated";
    final String updatedFullName = FULL_NAME + " (genauer: St. Johann)";
    final String updatedUsername = USERNAME + "_fromAustria";
    final String updatedProfileUrl = PROFILE_URL + "/myprofile";
    userService.insertOrUpdate(new User(ID, updatedUsername, updatedFullName, updatedAvatarUrl, updatedProfileUrl));

    // verify update:
    users = userService.findAll();
    assertEquals(1, users.size());
    loaded = users.get(0);
    assertEquals(ID, loaded.id.intValue());
    assertEquals(updatedAvatarUrl, loaded.avatarUrl);
    assertEquals(updatedUsername, loaded.username);
  }

  @Test
  public void findAllOrgaMembers() {
    // create one user that is NOT allowed to login:
    userService.insertOrUpdate(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));
    User user = userRepository.findOne(ID);
    assertFalse(user.canLogin);

    // create three users that are allowed - with unsorted usernames:
    final List<String> usernames = Arrays.asList("lalala", "bla", "blubb");
    usernames.forEach(u -> {
      User orgUser = new User(u.length(), u);
      orgUser.canLogin = CAN_LOGIN;
      userService.insertOrUpdate(orgUser);
    });

    // verify the three allowed users are returned:
    List<User> orgaMembers = userService.findAllOrgaMembers();
    orgaMembers.forEach(mem -> assertTrue(mem.canLogin));
    orgaMembers.forEach(mem -> assertFalse(mem.id.intValue() == ID));
    orgaMembers.forEach(mem -> assertFalse(mem.username.equals(USERNAME)));
    assertEquals(3, orgaMembers.size());

    // verify alphabetical sort order:
    assertEquals(usernames.get(0), orgaMembers.get(2).username);
    assertEquals(usernames.get(1), orgaMembers.get(0).username);
    assertEquals(usernames.get(2), orgaMembers.get(1).username);
  }

  @Test
  public void login() {
    final User user = new User(ID, USERNAME, FULL_NAME, AVATAR_URL, CAN_LOGIN, PROFILE_URL, null);

    when(userSession.getUser()).thenReturn(user);
    userRepository.save(user);
    userService.login(ID);
    assertNotNull(userSession.getUser());
  }

  @Test
  public void requireLoginWithoutException() {
    when(userSession.getUser()).thenReturn(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));
    userService.requireLogin();
  }

  @Test
  public void whoAmIWorksFine() {
    when(userSession.getUser()).thenReturn(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));

    User iam = userService.whoAmI();
    assertNotNull(iam);
    assertEquals(ID, iam.id.intValue());
  }

  @Test(expected = LoginRequiredException.class)
  public void whoAmIFails() {
    userService.whoAmI();
  }

  @Test(expected = LoginRequiredException.class)
  public void requireLogin() {
    userService.requireLogin();
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void usernameUnique() {
    // create a user:
    userService.insertOrUpdate(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));

    // saving another user with same username but different id should fail:
    userService.insertOrUpdate(new User(ID + 7, USERNAME, FULL_NAME + " von Foo", AVATAR_URL + "_blah", PROFILE_URL
        + "_dsdfsdc"));
  }

  @Test(expected = JpaSystemException.class)
  public void userNeedsId() {
    userService.insertOrUpdate(new User(null, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));
  }

  @Test(expected = TransactionSystemException.class)
  public void userNeedsUsername() {
    userService.insertOrUpdate(new User(ID, "", FULL_NAME, AVATAR_URL, PROFILE_URL));
  }

  @Test
  public void saveValidLanguage() {
    userRepository.save(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));
    User user = userService.findById(ID).orElseThrow(() -> new NotFoundException("User not found"));
    assertNull(user.userSettings);

    UserSettings userSettings = new UserSettings();
    userSettings.language = "de";
    userService.updateUserSettings(ID, userSettings);
    user = userService.findById(ID).orElseThrow(() -> new NotFoundException("User not found"));
    assertNotNull(user.userSettings);
    assertEquals("de", user.userSettings.language);
  }

  @Test(expected = ConstraintViolationException.class)
  public void doNotSaveUserSettingsWithNonExistingLanguage() {
    userRepository.save(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, PROFILE_URL));

    UserSettings userSettings = new UserSettings();
    userSettings.language = "xx";
    userService.updateUserSettings(ID, userSettings);
  }

  @Test(expected = BadRequestException.class)
  public void tryToLoginWhenNotAllowed() {
    userRepository.save(new User(ID, USERNAME, FULL_NAME, AVATAR_URL, !CAN_LOGIN, PROFILE_URL, null));
    userService.login(ID);
  }

}
