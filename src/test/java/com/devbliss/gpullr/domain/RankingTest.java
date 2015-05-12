package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests especially the score calculation algorithm implemented by {@link PullRequest}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class RankingTest {

  @Test
  public void sameScoreForSameRelevantValues() {
    // values relevant for score are same for both pull requests:
    RankingData rankingData = new RankingData();
    rankingData.addToClosedCount(1);
    rankingData.addToSumOfComments(3);
    rankingData.addToSumOfFilesChanged(7);
    rankingData.addToSumOfLinesAdded(71);
    rankingData.addToSumOfLinesRemoved(38);

    // create two pull requests equal in terms of those relevant data but unequal concerning other
    // data:
    Ranking r0 = new Ranking(rankingData, new User());
    Ranking r1 = new Ranking(rankingData, new User());

    // verify they have same score:
    assertEquals(r0.getScore().doubleValue(), r1.getScore().doubleValue(), 0.0001);
  }

  @Test
  public void baseScoreForTrivialPullRequest() {
    // create a pull request with all score relevant data being 0:
    RankingData rankingData = new RankingData();
    rankingData.addToSumOfLinesAdded(0);
    rankingData.addToSumOfLinesRemoved(0);
    rankingData.addToSumOfComments(0);
    rankingData.addToSumOfFilesChanged(0);
    rankingData.addToClosedCount(0);

    // make sure the calculated score is > 0 due to the base score every pull request gets:
    assertEquals(0d, new Ranking(rankingData, new User()).getScore().doubleValue(), 0.0001d);

    rankingData.addToClosedCount(1);
    assertEquals(5d, new Ranking(rankingData, new User()).getScore().doubleValue(), 0.0001d);

    rankingData.addToClosedCount(1);
    assertEquals(10d, new Ranking(rankingData, new User()).getScore().doubleValue(), 0.0001d);

    rankingData.addToClosedCount(8);
    assertEquals(50d, new Ranking(rankingData, new User()).getScore().doubleValue(), 0.0001d);
  }

}
