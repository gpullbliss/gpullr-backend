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
    final int linesAdded = 71;
    final int linesRemoved = 38;
    final int numberOfComments = 3;
    final int filesChanged = 7;

    // create two pull requests equal in terms of those relevant data but unequal concerning other
    // data:
    Ranking r0 = new Ranking();

    r0.sumOfLinesAdded = linesAdded;
    r0.sumOfLinesRemoved = linesRemoved;
    r0.sumOfComments = numberOfComments;
    r0.sumOfFilesChanged = filesChanged;

    Ranking r1 = new Ranking();

    r1.sumOfLinesAdded = linesAdded;
    r1.sumOfLinesRemoved = linesRemoved;
    r1.sumOfComments = numberOfComments;
    r1.sumOfFilesChanged = filesChanged;

    // verify they have same score:
    r0.calculateScore();
    r1.calculateScore();

    assertEquals(r0.getScore().doubleValue(), r1.getScore().doubleValue(), 0.0001);
  }

  @Test
  public void baseScoreForTrivialPullRequest() {
    // create a pull request with all score relevant data being 0:
    Ranking ranking = new Ranking();
    ranking.sumOfLinesAdded = 0;
    ranking.sumOfLinesRemoved = 0;
    ranking.sumOfComments = 0;
    ranking.sumOfFilesChanged = 0;

    // make sure the calculated score is > 0 due to the base score every pull request gets:
    ranking.calculateScore();
    assertEquals(0d, ranking.getScore().doubleValue(), 0.0001d);

    ranking.closedCount = 1;
    ranking.calculateScore();
    assertEquals(5d, ranking.getScore().doubleValue(), 0.0001d);

    ranking.closedCount = 2;
    ranking.calculateScore();
    assertEquals(10d, ranking.getScore().doubleValue(), 0.0001d);

    ranking.closedCount = 10;
    ranking.calculateScore();
    assertEquals(50d, ranking.getScore().doubleValue(), 0.0001d);
  }
}
