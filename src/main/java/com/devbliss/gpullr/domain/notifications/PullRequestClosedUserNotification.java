package com.devbliss.gpullr.domain.notifications;

import com.devbliss.gpullr.domain.User;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by abluem on 03/06/15.
 */
@Entity
@DiscriminatorValue(UserNotificationType.PULLREQUEST_CLOSED)
public class PullRequestClosedUserNotification extends UserNotification {

  @ManyToOne(fetch = FetchType.EAGER)
  public User actor;

}
