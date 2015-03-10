package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Ranking;

import org.springframework.stereotype.Component;

@Component
public class RankingConverter {
  
  public RankingDto toDto(Ranking entity) {
    RankingDto dto = new RankingDto();
    dto.avatarUrl = entity.avatarUrl;
    dto.closedCount = entity.closedCount;
    dto.rank = entity.rank;
    dto.username = entity.username;
    return dto;
  }
}
