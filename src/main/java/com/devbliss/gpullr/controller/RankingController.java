package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ListDto;
import com.devbliss.gpullr.controller.dto.RankingConverter;
import com.devbliss.gpullr.controller.dto.RankingDto;
import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import com.devbliss.gpullr.service.RankingService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints to fetch ranking data.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RestController
@RequestMapping("/rankings")
public class RankingController {

  @Autowired
  private RankingService rankingService;

  @Autowired
  private RankingConverter rankingConverter;

  @RequestMapping(method = RequestMethod.GET)
  public ListDto<RankingDto> getRankingsForScope(
      @RequestParam(value = "rankingScope", required = true) String rankingScopeString) {
    RankingScope rankingScope = RankingScope.parse(rankingScopeString);
    return new ListDto<>(rankingService
      .findAllWithRankingScope(rankingScope)
      .orElse(new RankingList())
      .getRankings()
      .stream()
      .map(r -> rankingConverter.toDto(r))
      .collect(Collectors.toList()));
  }
}
