package com.devbliss.gpullr.service;

import static java.lang.Math.log;

import com.devbliss.gpullr.domain.PullRequest;
import org.springframework.stereotype.Service;

@Service
public class PullRequestScoreCalculator {

  private static final double WEIGHT_LINES_OF_CODE = 1d;
  private static final double WEIGHT_NUMBER_OF_COMMENTS = 1d;
  private static final double WEIGHT_NUMBER_OF_FILES = 10d;

  private static final double LINES_OF_CODE_WEIGHT_SLOPE = 0.005d;

  public double calculateScore(PullRequest pullRequest) {
    return WEIGHT_LINES_OF_CODE * calcLinesOfCodeFactor(pullRequest)
        + WEIGHT_NUMBER_OF_COMMENTS * calcNumberOfCommentsFactor(pullRequest)
        + WEIGHT_NUMBER_OF_FILES * calcNumberOfFilesFactor(pullRequest)
        + 5;
  }

  private double calcNumberOfCommentsFactor(PullRequest pullRequest) {
    double cm = pullRequest.numberOfComments;
    return (cm < 1) ? 0 : log(cm) / log(2);
  }

  private double calcNumberOfFilesFactor(PullRequest pullRequest) {
    double fc = pullRequest.filesChanged;
    return log(fc) / log(2);
  }

  private double calcLinesOfCodeFactor(PullRequest pullRequest) {
    double loc = pullRequest.linesAdded - pullRequest.linesRemoved;
    double locLog = log(Math.abs(loc)) / log(2);
    locLog = (locLog < 1) ? 0 : locLog;

    return LINES_OF_CODE_WEIGHT_SLOPE * loc + locLog;
  }
}
