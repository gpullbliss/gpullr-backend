package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.CacheAge;
import com.devbliss.gpullr.repository.CacheAgeRepository;
import java.time.LocalDate;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Decides whether our locally stored GitHub data need refresh or are up to date. If yes, a full fetch should be performed, 
 * otherwise it's okay for this application to only listen to events, if not.
 * 
 * The decision is made based on the fact that pulling all events for a certain repo on 2015-02-10 returned
 * events back to 2014-12-17, the oldest event being different depending on the exact time the call was made, 
 * which immediately suggests that there is a soft limit somewhere around 55 days. 
 * Since this value may vary and is not documented, we will go up a bit lower:  
 * 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class CacheAgeService {

  private static final int MAX_AGE_IN_DAYS = 25;

  private final CacheAgeRepository cacheAgeRepository;

  @Autowired
  public CacheAgeService(CacheAgeRepository cacheAgeRepository) {
    this.cacheAgeRepository = cacheAgeRepository;
  }

  public boolean isRefreshRequired() {
    return StreamSupport
      .stream(cacheAgeRepository.findAll().spliterator(), false)
      .findFirst()
      .map(ca -> isRefreshRequired(ca.lastFullFetch))
      .orElse(true);
  }

  public void setRefreshedToday() {
    CacheAge cacheAge = StreamSupport
      .stream(cacheAgeRepository.findAll().spliterator(), false)
      .findFirst().orElse(new CacheAge());
    cacheAge.lastFullFetch = LocalDate.now(); 
    cacheAgeRepository.save(cacheAge);
  }

  private boolean isRefreshRequired(LocalDate lastFullFetch) {
    return lastFullFetch.isBefore(LocalDate.now().minusDays(MAX_AGE_IN_DAYS));
  }
}
