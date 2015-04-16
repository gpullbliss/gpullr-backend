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
import javax.validation.constraints.Min;

/**
 * Created by Alexander Bluem and Henning Schuetz on 15/04/15.
 */
@Entity
public class Notification {
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
  public NotificationType notificationType;
}
