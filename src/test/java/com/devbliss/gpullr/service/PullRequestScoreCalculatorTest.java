package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public class PullRequestScoreCalculatorTest {

  private static final String PRS_CSV_FILE = "src/test/resources/prs_for_scoring.csv";

  private static final String SEP = "\\|";

  private List<PullRequest> pullRequests;

  private PullRequestScoreCalculator pullRequestScoreCalculator;

  @Before
  public void setup() throws IOException {
    pullRequests = readPullRequests();
    pullRequestScoreCalculator = new PullRequestScoreCalculator();
  }

  @Test
  public void calculateScores() {
    pullRequests
        .stream()
        .forEach(pr ->
            System.out.println(String.format(
                "PR: files: %d\tAdded: %d\tRemoved: %d\tComments: %d\tScore: %.4f",
                pr.filesChanged, pr.linesAdded, pr.linesRemoved, pr.numberOfComments, pullRequestScoreCalculator.calculateScore(pr))));
  }

  private List<PullRequest> readPullRequests() throws IOException {

    try (BufferedReader br = Files.newBufferedReader(Paths.get(PRS_CSV_FILE))) {
      return br
          .lines()
          .skip(1)
          .filter(l -> !l.trim().isEmpty())
          .map(this::lineToPullRequest)
          .collect(Collectors.toList());
    }
  }

  /**
   * FILESCHANGED    LINESADDED    LINESREMOVED    NUMBEROFCOMMENTS
   *
   * @param line
   * @return
   */
  private PullRequest lineToPullRequest(String line) {
    String[] fields = line.split(SEP);
    PullRequest pullRequest = new PullRequest();
    pullRequest.filesChanged = Integer.parseInt(fields[0].trim());
    pullRequest.linesAdded = Integer.parseInt(fields[1].trim());
    pullRequest.linesRemoved = Integer.parseInt(fields[2].trim());
    pullRequest.numberOfComments = Integer.parseInt(fields[3].trim());
    return pullRequest;
  }

}
