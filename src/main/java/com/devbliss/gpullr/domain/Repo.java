package com.devbliss.gpullr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A repository at GitHub, i.e. a project whose pull requests are handled by this application.
 * Belongs to our organization in terms of GitHub permissions.
 * <p>
 * The id the one given by GitHub!
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Entity
public class Repo {

  @Id
  @NotNull
  @Min(1)
  public Integer id;

  @NotBlank
  @Column(unique = true)
  public String name;

  @Column(nullable = true,
          length = 1000)
  public String description;

  public Repo() {
  }

  public Repo(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

}
