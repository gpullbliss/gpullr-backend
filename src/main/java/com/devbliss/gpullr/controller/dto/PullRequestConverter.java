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
    dto.repoName = entity.repo.name;
    dto.author = entity.author;
    dto.createdAt = entity.createdAt.toOffsetDateTime().toString();
    dto.filesChanged = entity.filesChanged;
    dto.linesAdded = entity.linesAdded;
    dto.linesRemoved = entity.linesRemoved;
    dto.status = entity.state.name();
    dto.assignee = entity.assignee;
    dto.number = entity.number;
    dto.assignedAt = entity.assignedAt != null ? entity.assignedAt.toOffsetDateTime().toString() : null;
    dto.closedAt = entity.closedAt != null ? entity.closedAt.toString() : null;
    dto.numberOfComments = entity.numberOfReviewComments;
    dto.buildStatus = entity.buildStatus != null ? entity.buildStatus.state.name() : null;
    return dto;
  }

}
