package com.devbliss.gpullr.domain;

import org.springframework.context.ApplicationEvent;

/**
 * To be thrown when a new {@link Repo} has been stored in this application.
 * Not for updates!
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public class RepoCreatedEvent extends ApplicationEvent {

  private static final long serialVersionUID = -3435524172840811610L;

  public final Repo createdRepo;

  public RepoCreatedEvent(Object source, Repo createdRepo) {
    super(source);
    this.createdRepo = createdRepo;
  }
}
