package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.Pullrequest;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.Pullrequest} and 
 * {@link com.devbliss.gpullr.controller.dto.PullrequestDto} objects.
 */
@Component
public class PullrequestConverter {

  public PullrequestDto toDto(Pullrequest entity) {

    PullrequestDto dto = new PullrequestDto();
    dto.id = entity.id;
    dto.title = entity.title;
    dto.url = entity.url;
    dto.repository = entity.repo.name;
    dto.author = entity.owner;
    dto.creationDate = entity.createdAt.toString();
    dto.filesChanged = entity.changedFiles;
    dto.linesAdded = entity.additions;
    dto.linesRemoved = entity.deletions;
    dto.status = entity.state.toString();

    return dto;
  }

  public Pullrequest toEntity(PullrequestDto dto) {
    // TODO: implement
    return null;
  }

}
