package com.devbliss.gpullr.domain;

import static java.lang.Math.log;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents a pullRequest persisted in our application.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Entity
public class PullRequest {

  private static final double WEIGHT_LINES_OF_CODE = 5d;

  private static final double WEIGHT_NUMBER_OF_COMMENTS = 1d;

  private static final double WEIGHT_NUMBER_OF_FILES = 3d;

  private static final double MINIMAL_SCORE = 5d;

  private static final double WEIGHT_NEGATIVE_LINES_OF_CODE = .5d;

  private static final String SEPARATOR = " / ";

  public enum State {
    OPEN, CLOSED
  }

  @Id
  @NotNull
  @Min(1)
  public Integer id;

  @ManyToOne(optional = false)
  public Repo repo;

  @ManyToOne(optional = false)
  public User author;

  @ManyToOne(optional = true)
  public User assignee;

  public String title;

  @Column(unique = true)
  public String url;

  public ZonedDateTime createdAt;

  public ZonedDateTime closedAt;

  @NotNull
  @Enumerated(EnumType.STRING)
  public State state;

  /**
   * Number of the PR (unique only within the same repository!).
   */
  public Integer number;

  public Integer linesAdded;

  public Integer linesRemoved;

  public Integer filesChanged;

  @Column(nullable = true)
  public ZonedDateTime assignedAt;

  @Embedded
  public BuildStatus buildStatus;

  /**
   * Very often, but  NOT necessarily equal to the pull request title
   */
  public String branchName;

  /**
   * Sum of number of "review comments" (=refering to certain lines of code) and "comments" (belonging to the
   * pull request itself).
   */
  @NotNull
  public int numberOfComments;

  /**
   * Calculates the score for this pull request. It values the effort the assignee has to put in reviewing this
   * pull requests and is taken into account for the ranking calculation.
   * <p>
   * It uses the lines of changed code, the number of changed files and the number of comments written by the
   * assignee.Additionally, there is a basic score of {@link #MINIMAL_SCORE} every pull request gets even if the
   * values mentioned above are all 0 - to honour the effort it costs to assign and review a pull request no matter
   * how trivial or small it may be.
   *
   * @return
   */
  public Double calculateScore() {
    return WEIGHT_LINES_OF_CODE * calcLinesOfCodeFactor()
        + WEIGHT_NUMBER_OF_COMMENTS * calcNumberOfCommentsFactor()
        + WEIGHT_NUMBER_OF_FILES * calcNumberOfFilesFactor()
        + MINIMAL_SCORE;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    PullRequest other = (PullRequest) obj;

    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "[id=" + repo.id + SEPARATOR + repo.name + SEPARATOR + number + "]";
  }

  private double calcNumberOfCommentsFactor() {
    double cm = numberOfComments;
    return (cm < 1) ? 0 : log(cm) / log(2);
  }

  private double calcNumberOfFilesFactor() {
    double fc = filesChanged;
    return fc < 1 ? 0 : log(fc) / log(2);
  }

  private double calcLinesOfCodeFactor() {
    double loc = linesAdded - linesRemoved;
    double locLog = log(Math.abs(loc)) / log(2);

    locLog = (locLog < 1) ? 0 : locLog;

    if (loc < 0) {
      return locLog * WEIGHT_NEGATIVE_LINES_OF_CODE;
    }

    return locLog;
  }
}
