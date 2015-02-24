package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.PullRequestConverter;
import com.devbliss.gpullr.controller.dto.PullRequestDto;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserService;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage pull requests.
 */
@RestController
@RequestMapping("/pulls")
public class PullRequestController {

  @Autowired
  private PullRequestService pullRequestService;

  @Autowired
  private PullRequestConverter pullRequestConverter;

  @Autowired
  private UserService userService;

  @RequestMapping(method = RequestMethod.GET)
  public ListDto<PullRequestDto> findAll() {
    return new ListDto<PullRequestDto>(pullRequestService
      .findAllOpen()
      .stream()
      .map(pr -> pullRequestConverter.toDto(pr))
      .collect(Collectors.toList()));
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{pullRequestId}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void assignPullRequest(@PathVariable @NotNull Integer pullRequestId) {
    pullRequestService.assignPullRequest(userService.whoAmI(), pullRequestId);
  }

}