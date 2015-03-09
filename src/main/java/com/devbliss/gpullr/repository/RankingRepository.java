package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.Ranking;
import com.devbliss.gpullr.domain.RankingScope;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RankingRepository extends CrudRepository<Ranking, Long> {

  List<Ranking> findByRankingScopeOrderByNumberOfMergedPullRequests(RankingScope rankingScope);

}
