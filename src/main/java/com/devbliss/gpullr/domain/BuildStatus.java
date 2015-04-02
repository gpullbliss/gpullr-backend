package com.devbliss.gpullr.domain;

import javax.persistence.Column;

import java.time.ZonedDateTime;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Represents the current CI build status of a pull request or branch respectively.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Embeddable
public class BuildStatus {

  public enum State {
    SUCCESS, PENDING, FAILURE;

    public static State parse(String stringRepresentation) {
      return valueOf(stringRepresentation.toUpperCase());
    }
  }

  @Enumerated(EnumType.STRING)
  @Column(name="build_state")
  public State state;

  @Column(name="build_timestamp")
  public ZonedDateTime timestamp;
}
