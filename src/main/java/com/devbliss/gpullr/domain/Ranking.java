package com.devbliss.gpullr.domain;

import static java.lang.Math.log;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Aggregates all values of pull requests that are taken into account for score calculation.
 * {@link #calculateScore()} must be called manually after all values have been set in order to calculate the score
 * which is then stored in {@link #score}.
 *  
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
@Table(name = "rankings")
public class Ranking {

  private static final double WEIGHT_LINES_OF_CODE = 5d;

  private static final double WEIGHT_NUMBER_OF_COMMENTS = 1d;

  private static final double WEIGHT_NUMBER_OF_FILES = 3d;

  private static final double MINIMAL_SCORE = 5d;

  private static final double WEIGHT_NEGATIVE_LINES_OF_CODE = .5d;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @ManyToOne(fetch = FetchType.EAGER)
  private User user;

  @NotNull
  @Min(0)
  private int rank;

  @Min(0)
  private int sumOfLinesRemoved;

  @Min(0)
  private int sumOfLinesAdded;

  @Min(0)
  private int sumOfFilesChanged;

  @NotNull
  private Double score;

  @Min(0)
  private int closedCount;

  @Min(0)
  private int sumOfComments;

  /**
   * Only for JPA.
   */
  @SuppressWarnings("unused")
  private Ranking() {}

  public Ranking(RankingData rankingData, User user) {
    this.sumOfLinesAdded = rankingData.getSumOfLinesAdded();
    this.sumOfLinesRemoved = rankingData.getSumOfLinesRemoved();
    this.sumOfFilesChanged = rankingData.getSumOfFilesChanged();
    this.sumOfComments = rankingData.getSumOfComments();
    this.closedCount = rankingData.getClosedCount();
    this.user = user;
    score = calculateScore();
  }

  public Double getScore() {
    return score;
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public int getSumOfLinesRemoved() {
    return sumOfLinesRemoved;
  }

  public int getSumOfLinesAdded() {
    return sumOfLinesAdded;
  }

  public int getSumOfFilesChanged() {
    return sumOfFilesChanged;
  }

  public int getClosedCount() {
    return closedCount;
  }

  public int getSumOfComments() {
    return sumOfComments;
  }

  @Override
  public String toString() {
    return "Ranking{" +
        "id=" + id +
        ", user=" + user +
        ", rank=" + rank +
        ", closedCount=" + closedCount +
        ", sumOfFilesChanged=" + sumOfFilesChanged +
        ", sumOfLinesRemoved=" + sumOfLinesRemoved +
        ", sumOfLinesAdded=" + sumOfLinesAdded +
        ", sumOfScores=" + score +
        ", sumOfComments=" + sumOfComments +
        '}';
  }

  /**
   * Calculates the score for this ranking. It values the effort the assignee has to put in reviewing the
   * pull requests this ranking aggregates.
   * <p>
   * It uses the lines of changed code, the number of changed files and the number of comments written by the
   * assignee.Additionally, there is a basic score of {@link #MINIMAL_SCORE} every pull request gets even if the
   * values mentioned above are all 0 - to honour the effort it costs to assign and review a pull request no matter
   * how trivial or small it may be.
   *
   */
  private Double calculateScore() {
    return WEIGHT_LINES_OF_CODE * calcLinesOfCodeFactor()
        + WEIGHT_NUMBER_OF_COMMENTS * calcNumberOfCommentsFactor()
        + WEIGHT_NUMBER_OF_FILES * calcNumberOfFilesFactor()
        + MINIMAL_SCORE * closedCount;
  }

  private double calcNumberOfCommentsFactor() {
    double cm = sumOfComments;
    return (cm < 1) ? 0 : log(cm) / log(2);
  }

  private double calcNumberOfFilesFactor() {
    double fc = sumOfFilesChanged;
    return fc < 1 ? 0 : log(fc) / log(2);
  }

  private double calcLinesOfCodeFactor() {
    double loc = sumOfLinesAdded - sumOfLinesRemoved;
    double locLog = log(Math.abs(loc)) / log(2);

    locLog = (locLog < 1) ? 0 : locLog;

    if (loc < 0) {
      return locLog * WEIGHT_NEGATIVE_LINES_OF_CODE;
    }

    return locLog;
  }
}
