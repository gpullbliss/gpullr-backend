package com.devbliss.gpullr.domain;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
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
    PullRequest pr0 = new PullRequest();
    pr0.title = "Some Title";
    pr0.closedAt = ZonedDateTime.now();
    pr0.assignee = new User(1234, "eager user");

    pr0.linesAdded = linesAdded;
    pr0.linesRemoved = linesRemoved;
    pr0.numberOfComments = numberOfComments;
    pr0.filesChanged = filesChanged;

    PullRequest pr1 = new PullRequest();
    pr1.title = "Some completely different title";
    pr1.closedAt = ZonedDateTime.now().minusMonths(3);
    pr1.assignee = new User(1234, "some completely different user");

    pr1.linesAdded = linesAdded;
    pr1.linesRemoved = linesRemoved;
    pr1.numberOfComments = numberOfComments;
    pr1.filesChanged = filesChanged;

    // verify they have same score:
    assertEquals(pr0.calculateScore().doubleValue(), pr1.calculateScore().doubleValue(), 0.0001);
  }

  @Test
  public void baseScoreForTrivialPullRequest() {
    // create a pull request with all score relevant data being 0:
    PullRequest pr = new PullRequest();
    pr.linesAdded = 0;
    pr.linesRemoved = 0;
    pr.numberOfComments = 0;
    pr.filesChanged = 0;

    // make sure the calculated score is > 0 due to the base score every pull request gets:
    assertEquals(5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 1;
    assertEquals(5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 2;
    assertEquals(10d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 10;
    assertEquals(21.6096d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 10;
    pr.linesRemoved = 10;
    assertEquals(5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 0;
    pr.linesRemoved = 10;
    assertEquals(13.3048d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 9;
    pr.linesRemoved = 10;
    assertEquals(5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 9;
    pr.linesRemoved = 11;
    assertEquals(7.5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.linesAdded = 1009;
    pr.linesRemoved = 1011;
    assertEquals(7.5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 1;
    pr.linesAdded = 0;
    pr.linesRemoved = 0;
    assertEquals(5d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 0;
    assertEquals(8d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 1;
    assertEquals(8d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 2;
    assertEquals(13d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 2;
    pr.numberOfComments = 1;
    assertEquals(13d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 2;
    pr.numberOfComments = 2;
    assertEquals(14d, pr.calculateScore().doubleValue(), 0.0001d);

    pr.filesChanged = 2;
    pr.linesAdded = 2;
    pr.numberOfComments = 3;
    assertEquals(14.5849d, pr.calculateScore().doubleValue(), 0.0001d);

  }

}
