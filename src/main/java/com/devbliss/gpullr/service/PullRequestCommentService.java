package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.PullRequestComment;
import com.devbliss.gpullr.repository.PullRequestCommentRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PullRequestCommentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestCommentService.class);

  private final PullRequestCommentRepository pullRequestCommentRepository;

  @Autowired
  public PullRequestCommentService(
      PullRequestCommentRepository pullRequestCommentRepository) {
    this.pullRequestCommentRepository = pullRequestCommentRepository;
  }

  public void save(List<PullRequestComment> pullRequestComments) {
    pullRequestCommentRepository.save(pullRequestComments);
  }
}
