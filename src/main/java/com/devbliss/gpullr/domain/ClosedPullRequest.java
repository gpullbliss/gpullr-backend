package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Stores the information that a certain user has closed a certain pull request at a certain time.
 * Is used by the deferred ranking calculation process.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
public class ClosedPullRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @NotNull
  @Column(nullable = false)
  public ZonedDateTime closedAt;

  @NotBlank
  @Column(unique = true)
  public String pullRequestUrl;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  public User user;

  public ClosedPullRequest() {}

  public ClosedPullRequest(User user, ZonedDateTime closedAt, String pullRequestUrl) {
    this.user = user;
    this.closedAt = closedAt;
    this.pullRequestUrl = pullRequestUrl;
  }
}
