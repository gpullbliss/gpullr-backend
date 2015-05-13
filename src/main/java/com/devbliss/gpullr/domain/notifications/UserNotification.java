package com.devbliss.gpullr.domain.notifications;

import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.domain.User;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * A notification addressed to a single user that will show up until the user marks it as "seen".
 * Informs the user about events he might be interested in, e.g. that one of his pull requests have been merged.
 * 
 */
@Entity
@Table(name = "Notification")
public class UserNotification {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @Column(name = "TMSTMP")
  public ZonedDateTime timestamp;

  public Boolean seen;

  @Min(1)
  public long receivingUserId;

  @ManyToOne(fetch = FetchType.EAGER)
  public User actor;

  @OneToOne(fetch = FetchType.EAGER)
  public PullRequest pullRequest;

  @Enumerated(value = EnumType.STRING)
  public UserNotificationType notificationType;
}
