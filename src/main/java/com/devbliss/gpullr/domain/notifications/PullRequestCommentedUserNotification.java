package com.devbliss.gpullr.domain.notifications;

import com.devbliss.gpullr.domain.PullRequestComment;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * Created by abluem on 03/06/15.
 */
@Entity
@DiscriminatorValue(UserNotificationType.PULLREQUEST_COMMENTED)
public class PullRequestCommentedUserNotification extends UserNotification {

  public int count;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "NOTIFICATION_COMMENTS",
      joinColumns = @JoinColumn(name = "NOTIFICATION_ID"),
      inverseJoinColumns = @JoinColumn(name = "COMMENT_ID")
      )
  public List<PullRequestComment> comments;

}
