package com.devbliss.gpullr.controller.dto;

import com.devbliss.gpullr.domain.PullRequest;
import org.springframework.stereotype.Component;

/**
 * Converter for {@link com.devbliss.gpullr.domain.PullRequest} and 
 * {@link com.devbliss.gpullr.controller.dto.PullRequestDto} objects.
 */
@Component
public class PullRequestConverter {

  public PullRequestDto toDto(PullRequest entity) {

    PullRequestDto dto = new PullRequestDto();
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
    dto.assignee = entity.assignee;
    return dto;
  }
}
