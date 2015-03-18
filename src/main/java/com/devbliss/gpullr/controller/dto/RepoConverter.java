package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Repo;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.Repo} and {@link com.devbliss.gpullr.controller.dto.RepoDto} objects.
 */
@Component
public class RepoConverter {

  public RepoDto toDto(Repo entity) {
    RepoDto dto = new RepoDto();
    dto.id = entity.id;
    dto.name = entity.name;

    return dto;
  }

}
