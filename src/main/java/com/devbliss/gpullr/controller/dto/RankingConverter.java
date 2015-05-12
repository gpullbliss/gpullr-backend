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
    dto.rank = entity.getRank();
    dto.closedCount = entity.getClosedCount();
    dto.sumOfScores = entity.getScore();
    dto.sumOfFilesChanged = entity.getSumOfFilesChanged();
    dto.sumOfLinesAdded = entity.getSumOfLinesAdded();
    dto.sumOfLinesRemoved = entity.getSumOfLinesRemoved();
    dto.user = userConverter.toMinimalDto(entity.getUser());
    return dto;
  }

}
