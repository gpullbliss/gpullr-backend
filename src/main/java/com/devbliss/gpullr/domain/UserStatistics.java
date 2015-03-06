package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Statistical data about how many pull requests a certain user has merged.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
public class UserStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int id;

  @OneToOne(optional = false, fetch = FetchType.EAGER)
  @NotNull
  private User user;

  @ElementCollection
  @NotNull
  private List<UserHasClosedPullRequest> closedPullRequests = new ArrayList<>();

  public void userHasClosedPullRequest(PullRequest pullRequest) {
    closedPullRequests.add(new UserHasClosedPullRequest(pullRequest));
  }

  public long getNumberOfClosedPullRequests(Optional<Integer> daysInPast) {

    if (daysInPast.isPresent()) {
      ZonedDateTime boarder = ZonedDateTime.now().minusDays(daysInPast.get());
      return closedPullRequests.stream().filter(uhcp -> !uhcp.closeDate.isBefore(boarder)).count();
    } else {
      return closedPullRequests.size();
    }
  }
}
