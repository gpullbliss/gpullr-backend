package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests especially the score calculation algorithm implemented by {@link PullRequest}.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class PullRequestTest {

  @Test
  public void sameScoreForSameRelevantValues() {
    // values relevant for score are same for both pull requests:
    final int linesAdded = 71;
    final int linesRemoved = 38;
    final int numberOfComments = 3;
    final int filesChanged = 7;

    // create two pull requests equal in terms of those relevant data but unequal concerning other
    // data:
    Ranking pr0 = new Ranking();

    pr0.sumOfLinesAdded = linesAdded;
    pr0.sumOfLinesRemoved = linesRemoved;
    pr0.sumOfComments = numberOfComments;
    pr0.sumOfFilesChanged = filesChanged;

    Ranking pr1 = new Ranking();

    pr1.sumOfLinesAdded = linesAdded;
    pr1.sumOfLinesRemoved = linesRemoved;
    pr1.sumOfComments = numberOfComments;
    pr1.sumOfFilesChanged = filesChanged;

    // verify they have same score:
    pr0.calculateScore();
    pr1.calculateScore();

    assertEquals(pr0.getScore().doubleValue(), pr1.getScore().doubleValue(), 0.0001);
  }

  @Test
  public void baseScoreForTrivialPullRequest() {
    // create a pull request with all score relevant data being 0:
    Ranking pr = new Ranking();
    pr.sumOfLinesAdded = 0;
    pr.sumOfLinesRemoved = 0;
    pr.sumOfComments = 0;
    pr.sumOfFilesChanged = 0;

    // make sure the calculated score is > 0 due to the base score every pull request gets:
    pr.calculateScore();
    assertEquals(5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 1;
    pr.calculateScore();
    assertEquals(5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 2;
    pr.calculateScore();
    assertEquals(10d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 10;
    pr.calculateScore();
    assertEquals(21.6096d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 10;
    pr.sumOfLinesRemoved = 10;
    pr.calculateScore();
    assertEquals(5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 0;
    pr.sumOfLinesRemoved = 10;
    pr.calculateScore();
    assertEquals(13.3048d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 9;
    pr.sumOfLinesRemoved = 10;
    pr.calculateScore();
    assertEquals(5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 9;
    pr.sumOfLinesRemoved = 11;
    pr.calculateScore();
    assertEquals(7.5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfLinesAdded = 1009;
    pr.sumOfLinesRemoved = 1011;
    pr.calculateScore();
    assertEquals(7.5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 1;
    pr.sumOfLinesAdded = 0;
    pr.sumOfLinesRemoved = 0;
    pr.calculateScore();
    assertEquals(5d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 0;
    pr.calculateScore();
    assertEquals(8d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 1;
    pr.calculateScore();
    assertEquals(8d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 2;
    pr.calculateScore();
    assertEquals(13d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 2;
    pr.sumOfComments = 1;
    pr.calculateScore();
    assertEquals(13d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 2;
    pr.sumOfComments = 2;
    pr.calculateScore();
    assertEquals(14d, pr.getScore().doubleValue(), 0.0001d);

    pr.sumOfFilesChanged = 2;
    pr.sumOfLinesAdded = 2;
    pr.sumOfComments = 3;
    pr.calculateScore();
    assertEquals(14.5849d, pr.getScore().doubleValue(), 0.0001d);
  }

  // @Test
  // public void problematicScenario() {
  // PullRequest prMatthias0 = new PullRequest();
  // prMatthias0.linesAdded = 178;
  // prMatthias0.linesRemoved = 20;
  // prMatthias0.filesChanged = 6;
  // prMatthias0.numberOfComments = 0;
  //
  // PullRequest prMatthias1 = new PullRequest();
  // prMatthias1.linesAdded = 121;
  // prMatthias1.linesRemoved = 11;
  // prMatthias1.filesChanged = 8;
  // prMatthias1.numberOfComments = 0;
  //
  // PullRequest prMatthias2 = new PullRequest();
  // prMatthias2.linesAdded = 62;
  // prMatthias2.linesRemoved = 12;
  // prMatthias2.filesChanged = 6;
  // prMatthias2.numberOfComments = 0;
  //
  // double scoreMatthias = prMatthias0.calculateScore() + prMatthias1.calculateScore() +
  // prMatthias2.calculateScore();
  // assertEquals(138.2, scoreMatthias, 0.5);
  // /*
  // * 1379 | 196 | 15 | 0 | feature/90_fairRankingAlgorithm | 2015-05-07T13:18:43Z 79 | 24 | 12 | 0
  // * | feature/90_fairRankingAlgorithm | 2015-05-07T13:18:48Z 4 | 3 | 2 | 0 |
  // * bug/rankingUserNamesShown | 2015-05-07T13:56:48Z
  // */
  //
  // PullRequest prDaniel0 = new PullRequest();
  // prDaniel0.linesAdded = 1379;
  // prDaniel0.linesRemoved = 196;
  // prDaniel0.filesChanged = 15;
  // prDaniel0.numberOfComments = 0;
  //
  // PullRequest prDaniel1 = new PullRequest();
  // prDaniel1.linesAdded = 79;
  // prDaniel1.linesRemoved = 24;
  // prDaniel1.filesChanged = 12;
  // prDaniel1.numberOfComments = 0;
  //
  // PullRequest prDaniel2 = new PullRequest();
  // prDaniel2.linesAdded = 4;
  // prDaniel2.linesRemoved = 3;
  // prDaniel2.filesChanged = 2;
  // prDaniel2.numberOfComments = 0;
  //
  // double scoreDaniel = prDaniel0.calculateScore() + prDaniel1.calculateScore() +
  // prDaniel2.calculateScore();
  // assertEquals(120.4, scoreDaniel, 0.5);
  //
  // // alternative way: aggregate values first:
  // PullRequest prMatthias = new PullRequest();
  // prMatthias.linesAdded = prMatthias0.linesAdded + prMatthias1.linesAdded +
  // prMatthias2.linesAdded;
  // prMatthias.linesRemoved = prMatthias0.linesRemoved + prMatthias1.linesRemoved +
  // prMatthias2.linesRemoved;
  // prMatthias.filesChanged = prMatthias0.filesChanged + prMatthias1.filesChanged +
  // prMatthias2.filesChanged;
  // prMatthias.numberOfComments = prMatthias0.numberOfComments + prMatthias1.numberOfComments
  // + prMatthias2.numberOfComments;
  //
  // PullRequest prDaniel = new PullRequest();
  // prDaniel.linesAdded = prDaniel0.linesAdded + prDaniel1.linesAdded + prDaniel2.linesAdded;
  // prDaniel.linesRemoved = prDaniel0.linesRemoved + prDaniel1.linesRemoved +
  // prDaniel2.linesRemoved;
  // prDaniel.filesChanged = prDaniel0.filesChanged + prDaniel1.filesChanged +
  // prDaniel2.filesChanged;
  // prDaniel.numberOfComments = prDaniel0.numberOfComments + prDaniel1.numberOfComments +
  // prDaniel2.numberOfComments;
  //
  // System.out.println("Score Matthias: " + prMatthias.calculateScore());
  // System.out.println("Score Daniel: " + prDaniel.calculateScore());
  // }
}
