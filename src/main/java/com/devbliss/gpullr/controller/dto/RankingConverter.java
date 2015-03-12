package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Ranking;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Convergts {@link Ranking} entities to {@link RankingDto}s.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class RankingConverter {

  public RankingDto toDto(Ranking entity) {
    RankingDto dto = new RankingDto();
    dto.avatarUrl = entity.user.avatarUrl;
    dto.closedCount = entity.closedCount;
    dto.username = entity.user.username;
    return dto;
  }

  /**
   * Converts a list of {@link Ranking} entities to a list of {@link RankingDto}, and adds an ascending numeric 
   * "rank" to each dto according to the order of the given entities list.
   * 
   * @param entities
   * @return
   */
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
