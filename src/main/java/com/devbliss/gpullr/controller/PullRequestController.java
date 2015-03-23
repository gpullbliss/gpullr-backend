package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.PullRequestConverter;
import com.devbliss.gpullr.controller.dto.PullRequestDto;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage pull requests.
 */
@RestController
@RequestMapping("/pulls")
public class PullRequestController {

  private final PullRequestService pullRequestService;

  private final PullRequestConverter pullRequestConverter;

  private final UserService userService;

  @Autowired
  public PullRequestController(
      PullRequestService pullRequestService,
      PullRequestConverter pullRequestConverter,
      UserService userService) {
    this.pullRequestConverter = pullRequestConverter;
    this.pullRequestService = pullRequestService;
    this.userService = userService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ListDto<PullRequestDto> findAllOpen(@RequestParam(value = "repos", required = false) String repos) {
    List<PullRequest> pullRequests;

    if (repos == null || repos.isEmpty()) {
      pullRequests = pullRequestService.findAllOpen();
    } else {
      pullRequests = pullRequestService.findAllOpen(repos.split(";"));
    }

    return new ListDto<>(pullRequests
      .stream()
      .map(pullRequestConverter::toDto)
      .collect(Collectors.toList()));
  }

  @RequestMapping(value = "/closed", method = RequestMethod.GET)
  public ListDto<PullRequestDto> findAllClosed() {
    return new ListDto<>(
        pullRequestService.findAllClosed()
          .stream()
          .map(pullRequestConverter::toDto)
          .collect(Collectors.toList()));
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{pullRequestId}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void assignPullRequest(@PathVariable @NotNull Integer pullRequestId) {
    pullRequestService.assignPullRequest(userService.whoAmI(), pullRequestId);
  }

}
