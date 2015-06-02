package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Commit;
import com.devbliss.gpullr.domain.PullRequest;
import com.devbliss.gpullr.repository.CommitRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles business logic and facades persistence for {@link Commit} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Service
public class CommitService {

  private final CommitRepository commitRepository;

  @Autowired
  public CommitService(CommitRepository commitRepository) {
    this.commitRepository = commitRepository;
  }

  public void saveCommitsIfRelevant(List<Commit> commits, PullRequest pullRequest) {
    commits.forEach(commit -> saveCommitIfRelevant(commit, pullRequest));
  }

  private void saveCommitIfRelevant(Commit commit, PullRequest pullRequest) {
    if (pullRequest.assignedAt.isBefore(commit.commitDate)) {
      commitRepository.save(commit);
    }
  }
}
