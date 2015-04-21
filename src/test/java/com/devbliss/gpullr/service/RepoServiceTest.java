package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.domain.RepoCreatedEvent;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class RepoServiceTest {

  private static final Integer ID = 15;

  private static final String NAME = "coolrepo";

  private static final String DESCRIPTION = "this is some description. ";

  @Autowired
  private RepoRepository repoRepository;

  private RepoService repoService;

  private ApplicationContext applicationContext;

  private ArgumentCaptor<RepoCreatedEvent> repoCreatedEventArgumentCaptor;

  @Before
  public void setup() {
    applicationContext = mock(ApplicationContext.class);
    repoService = new RepoService(repoRepository, applicationContext);

    repoCreatedEventArgumentCaptor = ArgumentCaptor.forClass(RepoCreatedEvent.class);
  }

  @After
  public void teardown() {
    repoRepository.deleteAll();
  }

  @Test
  public void setActiveSetsActive() {
    // make sure database is empty at the beginning:
    assertEquals(0, repoService.findAllActive().size());

    // create a list of three repos and store it:
    List<Repo> reposToActivate = new ArrayList<>();
    IntStream.of(0, 1).forEach(i -> reposToActivate.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(reposToActivate);

    // make sure those three repos are returned by the service:
    List<Repo> retrievedRepos = repoService.findAllActive();
    assertEquals(2, retrievedRepos.size());
    reposToActivate.forEach(r -> assertTrue(retrievedRepos.contains(r)));
  }

  @Test
  public void setActiveDeactivatesRepo() {
    // create 3 repos
    List<Repo> reposToActivate = new ArrayList<>();
    IntStream.of(0, 1, 2).forEach(i -> reposToActivate.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(reposToActivate);

    assertEquals(3, repoService.findAllActive().size());

    // call setActiveRepos with one repo missing (should deactivate the missing repo)
    reposToActivate.remove(2);
    repoService.setActiveRepos(reposToActivate);

    // there are only 2 active repos left
    assertEquals(2, repoService.findAllActive().size());
  }

  @Test
  public void setActiveFiresRepoCreatedEvent() {
    Repo repo = new Repo(ID, NAME, DESCRIPTION);
    repoService.setActiveRepos(Arrays.asList(repo));

    // verify event is fired
    verify(applicationContext).publishEvent(repoCreatedEventArgumentCaptor.capture());

    // verify event includes the created repo
    RepoCreatedEvent event = repoCreatedEventArgumentCaptor.getValue();
    assertEquals(ID, event.createdRepo.id);
  }

  @Test
  public void setActiveRepos() {
    // make sure database is empty at the beginning:
    assertEquals(0, repoService.findAllActive().size());

    // create a list of three repos and store it:
    List<Repo> createdRepos = new ArrayList<>();
    IntStream.of(0, 1, 2).forEach(i -> createdRepos.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(createdRepos);
    ArgumentCaptor<RepoCreatedEvent> repoCreatedEventCaptor = ArgumentCaptor.forClass(RepoCreatedEvent.class);
    verify(applicationContext, times(3)).publishEvent(repoCreatedEventCaptor.capture());
    repoCreatedEventCaptor.getAllValues().forEach(e -> assertTrue(createdRepos.contains(e.createdRepo)));
    reset(applicationContext);

    // make sure those three repos are returned by the service:
    List<Repo> retrievedRepos = repoService.findAllActive();
    assertEquals(3, retrievedRepos.size());
    createdRepos.forEach(r -> assertTrue(retrievedRepos.contains(r)));

    // remove one element from list, add two new ones and store the list again:
    Repo notStoredAgain = createdRepos.remove(2);
    IntStream.of(3, 4).forEach(i -> createdRepos.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(createdRepos);
    ArgumentCaptor<RepoCreatedEvent> repoCreatedEventCaptorAgain = ArgumentCaptor.forClass(RepoCreatedEvent.class);
    verify(applicationContext, times(2)).publishEvent(repoCreatedEventCaptorAgain.capture());
    repoCreatedEventCaptorAgain.getAllValues().forEach(e -> assertTrue(createdRepos.contains(e.createdRepo)));

    // the element that has NOT been stored again should not be returned by findAllActive() ...:
    List<Repo> retrievedReposAgain = repoService.findAllActive();
    assertEquals(4, retrievedReposAgain.size());
    createdRepos.forEach(r -> assertTrue(retrievedReposAgain.contains(r)));
    assertFalse(retrievedReposAgain.contains(notStoredAgain));

    // ... however, the repo should still exist (inactive):
    Optional<Repo> notStoredAgainFromDb = repoService.findByName(notStoredAgain.name);
    assertTrue(notStoredAgainFromDb.isPresent());
    assertFalse(notStoredAgainFromDb.get().active);
  }

  @Test
  public void findRepoByName() {
    final String name = NAME + "_nameSearch";
    final String description = DESCRIPTION;

    // first try without existing repository
    Optional<Repo> repo = repoService.findByName(name);
    assertFalse(repo.isPresent());

    // insert new repo to findByName first
    repoService.setActiveRepos(Arrays.asList(new Repo(ID, name, description)));

    repo = repoService.findByName(name);
    assertTrue(repo.isPresent());
    assertEquals(ID, repo.get().id);
    assertEquals(name, repo.get().name);
    assertEquals(description, repo.get().description);
  }
}
