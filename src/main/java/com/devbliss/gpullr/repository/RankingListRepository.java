package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.RankingList;
import com.devbliss.gpullr.domain.RankingScope;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RankingListRepository extends CrudRepository<RankingList, Long> {

  List<RankingList> findByRankingScopeOrderByCalculationDateDesc(RankingScope rankingScope);

  List<RankingList> findByCalculationDateBeforeAndRankingScope(
      ZonedDateTime calculationDate,
      RankingScope rankingScope);
}
