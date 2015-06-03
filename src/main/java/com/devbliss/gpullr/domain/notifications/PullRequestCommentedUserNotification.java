package com.devbliss.gpullr.domain.notifications;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by abluem on 03/06/15.
 */
@Entity
@DiscriminatorValue(UserNotificationType.PULLREQUEST_COMMENTED)
public class PullRequestCommentedUserNotification extends UserNotification {

  public int count;

}
