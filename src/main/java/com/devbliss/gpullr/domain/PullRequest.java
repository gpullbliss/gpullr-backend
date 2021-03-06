package com.devbliss.gpullr.domain;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents a pullRequest persisted in our application.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Entity
public class PullRequest {

  private static final String SEPARATOR = " / ";

  public enum State {
    OPEN, CLOSED;

    /**
     * Parses State from typical lower case string representation
     * 
     * @param stringRepresentation
     * @return
     */
    public static State parse(String stringRepresentation) {
      return valueOf(stringRepresentation.toUpperCase());
    }
  }

  @Id
  @NotNull
  @Min(1)
  public Integer id;

  @ManyToOne(optional = false)
  public Repo repo;

  @ManyToOne(optional = false)
  public User author;

  @ManyToOne(optional = true)
  public User assignee;

  public String title;

  @Column(unique = true)
  public String url;

  public ZonedDateTime createdAt;

  public ZonedDateTime updatedAt;

  public ZonedDateTime closedAt;

  @NotNull
  @Enumerated(EnumType.STRING)
  public State state;

  /**
   * Number of the PR (unique only within the same repository!).
   */
  public Integer number;

  public Integer linesAdded;

  public Integer linesRemoved;

  public Integer filesChanged;

  @Column(nullable = true)
  public ZonedDateTime assignedAt;

  @Embedded
  public BuildStatus buildStatus;

  /**
   * Very often, but  NOT necessarily equal to the pull request title
   */
  public String branchName;

  /**
   * Sum of number of "review comments" (=refering to certain lines of code) and "comments" (belonging to the
   * pull request itself).
   */
  @NotNull
  public int numberOfComments;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    PullRequest other = (PullRequest) obj;

    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    return "[id=" + repo.id + SEPARATOR + repo.name + SEPARATOR + number + "]";
  }
}
