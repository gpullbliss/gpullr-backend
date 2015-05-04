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
  }
}
