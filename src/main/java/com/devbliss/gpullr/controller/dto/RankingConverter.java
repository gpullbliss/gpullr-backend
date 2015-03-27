package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Ranking;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convergts {@link Ranking} entities to {@link RankingDto}s.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Component
public class RankingConverter {

  @Autowired
  private UserConverter userConverter;

  public RankingDto toDto(Ranking entity) {
    RankingDto dto = new RankingDto();
    dto.rank = entity.rank;
    dto.closedCount = entity.closedCount;
    dto.users = entity.users.stream().map(u -> userConverter.toMinimalDto(u)).collect(Collectors.toList());
    return dto;
  }

}
