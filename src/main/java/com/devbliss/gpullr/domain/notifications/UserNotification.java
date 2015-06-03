package com.devbliss.gpullr.domain.notifications;

import com.devbliss.gpullr.domain.PullRequest;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * A notification addressed to a single user that will show up until the user marks it as "seen".
 * Informs the user about events he might be interested in, e.g. that one of his pull requests have been merged.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "NOTIFICATIONTYPE")
@Table(name = "Notification")
public abstract class UserNotification {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @Column(name = "TMSTMP")
  public ZonedDateTime timestamp;

  public Boolean seen;

  @Min(1)
  public long receivingUserId;

  @OneToOne(fetch = FetchType.EAGER)
  public PullRequest pullRequest;

  @Column(name = "NOTIFICATIONTYPE",
      insertable = false,
      updatable = false)
  public String notificationType;
}
