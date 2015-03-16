package com.devbliss.gpullr.controller.dto;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.devbliss.gpullr.domain.RankingList;
import org.springframework.stereotype.Component;

@Component
public class RankingListConverter {
  
  @Autowired
  private RankingConverter rankingConverter;
  
  public RankingListDto toDto(RankingList entity) {
    RankingListDto dto = new RankingListDto();
    dto.calculationDate = entity.calculationDate.toString();
    dto.rankings = entity.getRankings().stream().map(rankingConverter::toDto).collect(Collectors.toList());
    dto.rankingScope = entity.rankingScope.name();
    return dto;
  }
}
