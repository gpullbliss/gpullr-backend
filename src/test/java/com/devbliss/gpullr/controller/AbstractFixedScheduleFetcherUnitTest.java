package com.devbliss.gpullr.controller;

import org.mockito.ArgumentCaptor;

import org.mockito.Captor;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.junit.Assert.assertEquals;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Mock based unit test for {@link AbstractFixedScheduleFetcher}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractFixedScheduleFetcherUnitTest {
  
  private class MockAbstractFixedScheduleFetcher extends AbstractFixedScheduleFetcher {
    
    private int fetchCallCounter;

    @Override
    protected Date nextFetch() {
      return NEXT_FETCH;
    }

    @Override
    protected void fetch() {
      fetchCallCounter++;
    }
    
    public int getFetchCallCounter() {
      return fetchCallCounter;
    }
  };
  
  private static final Date NEXT_FETCH = Date
      .from(LocalDateTime.of(2100, Month.APRIL, 1, 18, 9).atZone(ZoneId.systemDefault()).toInstant());

  @Mock
  private ThreadPoolTaskScheduler executor;
  
  @Mock
  private Logger logger;
  
  @Captor
  private ArgumentCaptor<Runnable> runnableCaptor;

  private MockAbstractFixedScheduleFetcher fetcher;

  @Before
  public void setup() {
    fetcher = new MockAbstractFixedScheduleFetcher();
    fetcher.setThreadPoolTaskScheduler(executor);
    fetcher.logger = logger;
  }

  @Test
  public void startFetchLoop() {
    // at the beginning fetch() should not have been called:
    assertEquals(0, fetcher.getFetchCallCounter());
    
    // immediately after starting the fetch loop, fetch() should have been called once:
    fetcher.startFetchLoop();
    assertEquals(1, fetcher.getFetchCallCounter());
    
    // next fetch should have been scheduled to the date the fetcher implementation has calculated:
    verify(executor).schedule(runnableCaptor.capture(), eq(NEXT_FETCH));
    
    // when running what has been scheduled, fetch() should have been called once more:
    runnableCaptor.getValue().run();
    assertEquals(2, fetcher.getFetchCallCounter());
  }
}
