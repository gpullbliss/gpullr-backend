package com.devbliss.gpullr.domain.notifications;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created by abluem on 15/04/15.
 */
@Entity
public class PullRequestClosedNotification extends Notification {
  @ManyToOne(fetch = FetchType.EAGER)
  public User assignee;

  @OneToOne(fetch = FetchType.EAGER)
  public PullRequest pullRequest;
}
