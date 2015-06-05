package com.devbliss.gpullr.service.github;

import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.domain.PullRequest;
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
    PullRequest pr;

    System.err.println("***** EVENT: " + event.toString());

    if (event.hasPullRequestUrl()) {
      pr = pullRequestService
          .findByUrl(event.pullRequestUrl)
          .orElseThrow(() -> new NotFoundException(String.format("No pull request found with html_url '%s' set", event.pullRequestUrl)));
    } else {
      pr = pullRequestService
          .findById(event.pullRequestId)
          .orElseThrow(() -> new NotFoundException("No pull request found with id " + event.pullRequestId));
    }
    System.err.println("***** found PULLREQUEST: " + pr.toString());

    comment.setPullRequest(pr);
    commentService.save(comment);
    userNotificationService.calculateCommentNotifications(comment.getPullRequest());
  }

}
