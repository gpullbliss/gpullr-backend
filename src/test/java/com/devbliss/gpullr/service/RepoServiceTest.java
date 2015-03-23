package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

  @Before
  public void setup() {
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    repoService = new RepoService(repoRepository, applicationContext);
  }

  @After
  public void teardown() {
    repoRepository.deleteAll();
  }

  @Test
  public void insertUpdateShowAll() {
    // make sure database is empty at the beginning:
    List<Repo> allRepos = repoService.findAll();
    assertEquals(0, allRepos.size());

    // insert new repo:
    repoService.insertOrUpdate(new Repo(ID, NAME, DESCRIPTION));

    // fetch all and make sure it is returned:
    allRepos = repoService.findAll();
    assertEquals(1, allRepos.size());

    // validate its values:
    Repo repo = allRepos.get(0);
    assertEquals(ID, repo.id);
    assertEquals(NAME, repo.name);
    assertEquals(DESCRIPTION, repo.description);

    // update repo and make sure it is really updated:
    final String updatedName = NAME + "_updated";
    final String updatedDescription = DESCRIPTION + "_upd8ted";
    repoService.insertOrUpdate(new Repo(ID, updatedName, updatedDescription));

    allRepos = repoService.findAll();
    assertEquals(1, allRepos.size());

    repo = allRepos.get(0);
    assertEquals(ID, repo.id);
    assertEquals(updatedName, repo.name);
    assertEquals(updatedDescription, repo.description);
  }

  @Test
  public void setActiveRepos() {
    // make sure database is empty at the beginning:
    assertEquals(0, repoService.findAll().size());

    // create a list of three repos and store it:
    List<Repo> createdRepos = new ArrayList<>();
    IntStream.of(0, 1, 2).forEach(i -> createdRepos.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(createdRepos);

    // make sure those three repos are returned by the service:
    List<Repo> retrievedRepos = repoService.findAll();
    assertEquals(3, retrievedRepos.size());
    createdRepos.forEach(r -> assertTrue(retrievedRepos.contains(r)));

    // remove one element from list, add two new ones and store the list again:
    Repo notStoredAgain = createdRepos.remove(2);
    IntStream.of(3, 4).forEach(i -> createdRepos.add(new Repo(ID + i, NAME + i, DESCRIPTION + i)));
    repoService.setActiveRepos(createdRepos);

    // the element that has NOT been stored again should still be there, but now inactive:
    List<Repo> retrievedReposAgain = repoService.findAll();
    assertEquals(5, retrievedReposAgain.size());
    createdRepos.forEach(r -> assertTrue(retrievedReposAgain.contains(r)));
    assertTrue(retrievedReposAgain.contains(notStoredAgain));
    assertFalse(retrievedReposAgain.get(retrievedReposAgain.indexOf(notStoredAgain)).active);
  }

  @Test
  public void findRepoByName() {
    final String name = NAME + "_nameSearch";
    final String description = DESCRIPTION;

    // first try without existing repository
    Optional<Repo> repo = repoService.findByName(name);
    assertFalse(repo.isPresent());

    // insert new repo to findByName first
    repoService.insertOrUpdate(new Repo(ID, name, description));

    repo = repoService.findByName(name);
    assertTrue(repo.isPresent());
    assertEquals(ID, repo.get().id);
    assertEquals(name, repo.get().name);
    assertEquals(description, repo.get().description);
  }
}
