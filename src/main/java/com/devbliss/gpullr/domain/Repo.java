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
  
  public boolean active = true;

  public Repo() {}

  public Repo(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

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

    Repo other = (Repo) obj;

    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
