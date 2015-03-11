package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Ranking;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RankingConverter {

  public RankingDto toDto(Ranking entity) {
    RankingDto dto = new RankingDto();
    dto.avatarUrl = entity.avatarUrl;
    dto.closedCount = entity.closedCount;
    dto.username = entity.username;
    return dto;
  }

  public List<RankingDto> toDtoListWithRank(List<Ranking> entities) {
    List<RankingDto> dtos = new ArrayList<>();

    for (int rank = 0; rank < entities.size(); rank++) {
      RankingDto dto = toDto(entities.get(rank));
      dto.rank = rank + 1;
      dtos.add(dto);
    }

    return dtos;
  }
}
