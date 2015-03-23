package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.RepoConverter;
import com.devbliss.gpullr.controller.dto.RepoDto;
import com.devbliss.gpullr.service.RepoService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage repositories
 */
@RestController
@RequestMapping("/repos")
public class RepoController {

  @Autowired
  private RepoService repoService;

  @Autowired
  private RepoConverter repoConverter;

  @RequestMapping
  public List<RepoDto> getAllRepos() {
    return repoService.findAll()
      .stream()
      .map(repoConverter::toDto)
      .collect(Collectors.toList());
  }

}
