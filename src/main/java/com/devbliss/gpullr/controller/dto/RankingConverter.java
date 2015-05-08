package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Ranking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Ranking} entities to {@link RankingDto}s.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Component
public class RankingConverter {

  @Autowired
  private UserConverter userConverter;

  public RankingDto toDto(Ranking entity) {
    RankingDto dto = new RankingDto();
    dto.rank = entity.rank;
    dto.closedCount = entity.closedCount;
    dto.sumOfScores = entity.getScore();
    dto.sumOfFilesChanged = entity.sumOfFilesChanged;
    dto.sumOfLinesAdded = entity.sumOfLinesAdded;
    dto.sumOfLinesRemoved = entity.sumOfLinesRemoved;
    dto.user = userConverter.toMinimalDto(entity.user);
    return dto;
  }

}
