package com.devbliss.gpullr.controller.dto;

import static org.junit.Assert.assertEquals;

import com.devbliss.gpullr.BaseTest;
import com.devbliss.gpullr.domain.Repo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Unit tests for {@link com.devbliss.gpullr.controller.dto.RepoConverter}.
 */
public class RepoConverterTest extends BaseTest {

  @InjectMocks
  private RepoConverter repoConverter;

  private Repo repo;

  @Before
  public void setup() {
    super.setUp();

    repo = new Repo();
    repo.id = 1;
    repo.name = "repo name";
    repo.description = "repo description";
  }

  @Test
  public void toDto() {
    RepoDto dto = repoConverter.toDto(repo);

    assertEquals(repo.id, dto.id);
    assertEquals(repo.name, dto.name);
  }

}