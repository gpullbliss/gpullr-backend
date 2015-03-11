package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.RankingConverter;
import com.devbliss.gpullr.controller.dto.RankingDto;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
public class RankingController {

  @Autowired
  private RankingService rankingService;

  @Autowired
  private RankingConverter rankingConverter;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListDto<RankingDto> getRankingsForScope(
      @RequestParam(value = "rankingScope", required = true) String rankingScopeString) {
    RankingScope rankingScope = RankingScope.parse(rankingScopeString);
    return new ListDto<>(rankingConverter.toDtoListWithRank(rankingService
      .findAllWithRankingScope(rankingScope)
      .orElse(new RankingList())
      .getRankings()
      ));
  }
}
