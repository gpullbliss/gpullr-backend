package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.PullrequestConverter;
import com.devbliss.gpullr.controller.dto.PullrequestDto;
import com.devbliss.gpullr.domain.Pullrequest;
import com.devbliss.gpullr.service.PullrequestService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
  PullrequestService pullrequestService;

  @Autowired
  PullrequestConverter pullrequestConverter;

  @RequestMapping(method = RequestMethod.GET)
  public List<PullrequestDto> getPullrequests() {
    List<Pullrequest> allPullrequests = pullrequestService.findAll();

    List<PullrequestDto> allDtos = new ArrayList<PullrequestDto>();

    for (Pullrequest pullrequest : allPullrequests  ) {
      allDtos.add(pullrequestConverter.toDto(pullrequest));
    }


    return allDtos;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{pullrequestId}")
  public void assignPullrequest(@RequestBody String sessionId, @PathVariable Integer pullrequestId) {
    pullrequestService.assignPullrequest(sessionId, pullrequestId);
  }

}
