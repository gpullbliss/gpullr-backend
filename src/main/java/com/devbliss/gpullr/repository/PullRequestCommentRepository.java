package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.PullRequestComment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PullRequestCommentRepository extends CrudRepository<PullRequestComment, Integer> {

  List<PullRequestComment> findAll();
}
