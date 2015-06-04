package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.domain.PullRequestCommentEvent;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.service.CommentService;
import com.devbliss.gpullr.service.PullRequestService;
import com.devbliss.gpullr.service.UserNotificationService;
import com.devbliss.gpullr.util.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles comment events fetched from GitHub and triggers the appropriate action in business layer.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Service
public class CommentEventHandler {

  @Log
  Logger logger;

  private final CommentService commentService;

  private final UserNotificationService userNotificationService;

  private final PullRequestService pullRequestService;

  @Autowired
  public CommentEventHandler(
      CommentService commentService,
      UserNotificationService userNotificationService,
      PullRequestService pullRequestService) {
    this.commentService = commentService;
    this.userNotificationService = userNotificationService;
    this.pullRequestService = pullRequestService;
  }

  public void handleCommentEvent(PullRequestCommentEvent event) {
    Comment comment = event.comment;
    comment.setPullRequest(pullRequestService
      .findById(event.pullRequestId)
      .orElseThrow(() -> new NotFoundException("No pull request found with id " + event.pullRequestId)));
    commentService.save(comment);
    userNotificationService.calculateCommentNotifications(comment.getPullRequest());
  }

}
