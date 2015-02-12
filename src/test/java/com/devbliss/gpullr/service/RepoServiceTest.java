package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.Repo;
import com.devbliss.gpullr.repository.RepoRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
    repoService = new RepoService(repoRepository);
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
    final String updatedDescription = DESCRIPTION + "_updated";
    repoService.insertOrUpdate(new Repo(ID, updatedName, updatedDescription));

    allRepos = repoService.findAll();
    assertEquals(1, allRepos.size());

    repo = allRepos.get(0);
    assertEquals(ID, repo.id);
    assertEquals(updatedName, repo.name);
    assertEquals(updatedDescription, repo.description);
  }
}
