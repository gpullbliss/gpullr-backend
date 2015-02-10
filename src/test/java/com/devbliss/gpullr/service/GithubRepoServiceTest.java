package com.devbliss.gpullr.service;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.Application;
import com.devbliss.gpullr.domain.GithubRepo;
import com.devbliss.gpullr.repository.GithubRepoRepository;
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
public class GithubRepoServiceTest {

  private static final Integer ID = 15;

  private static final String NAME = "coolrepo";

  private static final String DESCRIPTION = "this is some description. ";

  @Autowired
  private GithubRepoRepository githubRepoRepository;

  private GithubRepoService githubRepoService;

  @Before
  public void setup() {
    githubRepoService = new GithubRepoService(githubRepoRepository);
  }

  @Test
  public void insertUpdateShowAll() {
    // make sure database is empty at the beginning:
    List<GithubRepo> allRepos = githubRepoService.findAll();
    assertEquals(0, allRepos.size());

    // insert new repo:
    githubRepoService.insertOrUpdate(new GithubRepo(ID, NAME, DESCRIPTION));

    // fetch all and make sure it is returned:
    allRepos = githubRepoService.findAll();
    assertEquals(1, allRepos.size());

    // validate its values:
    GithubRepo repo = allRepos.get(0);
    assertEquals(ID, repo.id);
    assertEquals(NAME, repo.name);
    assertEquals(DESCRIPTION, repo.description);

    // update repo and make sure it is really updated:
    final String updatedName = NAME + "_updated";
    final String updatedDescription = DESCRIPTION + "_updated";
    githubRepoService.insertOrUpdate(new GithubRepo(ID, updatedName, updatedDescription));

    allRepos = githubRepoService.findAll();
    assertEquals(1, allRepos.size());

    repo = allRepos.get(0);
    assertEquals(ID, repo.id);
    assertEquals(updatedName, repo.name);
    assertEquals(updatedDescription, repo.description);
  }
}
