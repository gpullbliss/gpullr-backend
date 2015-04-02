package com.devbliss.gpullr.controller.dto;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.PullRequest.State;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.User;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Unittest for {@link PullRequestConverter}.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class PullRequestConverterUnitTest {

  private static final Integer ID = 1981;

  private static final String TITLE = "Awesome pull request";

  private static final String URL = "http://my.awesome.pullreq.est";

  private static final String REPO_NAME = "Su a dolles Rebbou";

  private static final Repo REPO = new Repo(1, REPO_NAME, "");

  private static final Integer OWNER_ID = 15;

  private static final String OWNER_USERNAME = "icke";

  private static final String OWNER_FULL_NAME = "Mr X";

  private static final String OWNER_AVATAR_URL = "http://myse.lf";

  private static final String OWNER_PROFILE_URL = "http://coolowner.example.com";

  private static final User OWNER = new User(OWNER_ID, OWNER_USERNAME, OWNER_FULL_NAME, OWNER_AVATAR_URL,
      OWNER_PROFILE_URL);

  private static final ZonedDateTime CREATED_AT = ZonedDateTime.of(LocalDateTime.of(2015, Month.APRIL, 1, 17, 15),
      ZoneId.of("Europe/Paris"));

  private static final ZonedDateTime ASSIGNED_AT = ZonedDateTime.of(LocalDateTime.of(2015, Month.APRIL, 1, 18, 15),
      ZoneId.of("Europe/Paris"));

  private static final String CREATED_AT_STRING = "2015-04-01T17:15+02:00";

  private static final String ASSIGNED_AT_STRING = "2015-04-01T18:15+02:00";

  private static final Integer FILES_CHANGED = 17;

  private static final Integer LINES_ADDED = 33;

  private static final Integer LINES_REMOVED = 13;

  private static final State STATE = State.CLOSED;

  private static final Integer ASSIGNEE_ID = 111;

  private static final String ASSIGNEE_USERNAME = "someone";

  private static final String ASSIGNEE_FULL_NAME = "Someone, Else";

  private static final String ASSIGNEE_AVATAR_URL = "http://you.jpg";

  private static final String ASSSIGNEE_PROFILE_URL = "http://assign.yourself.example.com";

  private static final User ASSIGNEE =
      new User(ASSIGNEE_ID, ASSIGNEE_USERNAME, ASSIGNEE_FULL_NAME, ASSIGNEE_AVATAR_URL, ASSSIGNEE_PROFILE_URL);

  private static final Integer NUMBER = 97;

  private PullRequest entity;

  private PullRequestConverter pullRequestConverter;

  @Before
  public void setup() {
    pullRequestConverter = new PullRequestConverter();
    entity = new PullRequest();
    entity.linesAdded = LINES_ADDED;
    entity.assignee = ASSIGNEE;
    entity.filesChanged = FILES_CHANGED;
    entity.createdAt = CREATED_AT;
    entity.linesRemoved = LINES_REMOVED;
    entity.id = ID;
    entity.number = NUMBER;
    entity.author = OWNER;
    entity.repo = REPO;
    entity.state = STATE;
    entity.title = TITLE;
    entity.url = URL;
    entity.assignedAt = ASSIGNED_AT;
  }

  @Test
  public void toDto() {
    PullRequestDto dto = pullRequestConverter.toDto(entity);
    assertEquals(LINES_ADDED, dto.linesAdded);
    assertEquals(ASSIGNEE_AVATAR_URL, dto.assignee.avatarUrl);
    assertEquals(ASSIGNEE_ID, dto.assignee.id);
    assertEquals(ASSIGNEE_USERNAME, dto.assignee.username);
    assertEquals(FILES_CHANGED, dto.filesChanged);
    assertEquals(CREATED_AT_STRING, dto.createdAt);
    assertEquals(LINES_REMOVED, dto.linesRemoved);
    assertEquals(ID, dto.id);
    assertEquals(NUMBER, dto.number);
    assertEquals(OWNER_AVATAR_URL, dto.author.avatarUrl);
    assertEquals(OWNER_ID, dto.author.id);
    assertEquals(OWNER_USERNAME, dto.author.username);
    assertEquals(REPO_NAME, dto.repoName);
    assertEquals(STATE.name(), dto.status);
    assertEquals(TITLE, dto.title);
    assertEquals(URL, dto.url);
    assertEquals(ASSIGNED_AT_STRING, dto.assignedAt);
  }
}
