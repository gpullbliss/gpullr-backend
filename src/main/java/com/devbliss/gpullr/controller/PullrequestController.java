package com.devbliss.gpullr.controller;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.constraints.NotNull;
import com.devbliss.gpullr.controller.dto.PullrequestConverter;
import com.devbliss.gpullr.controller.dto.PullrequestDto;
import com.devbliss.gpullr.service.PullrequestService;
import com.devbliss.gpullr.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to manage pull requests.
 */
@RestController
@RequestMapping("/pulls")
public class PullrequestController {

  @Autowired
  private PullrequestService pullrequestService;

  @Autowired
  private PullrequestConverter pullrequestConverter;

  @Autowired
  private UserService userService;

  @RequestMapping(method = RequestMethod.GET)
  public List<PullrequestDto> findAll() {
    return pullrequestService
      .findAllOpen()
      .stream()
      .map(pr -> pullrequestConverter.toDto(pr))
      .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{pullrequestId}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void assignPullrequest(@PathVariable @NotNull Integer pullrequestId) {
    pullrequestService.assignPullrequest(userService.whoAmI(), pullrequestId);
  }

}
