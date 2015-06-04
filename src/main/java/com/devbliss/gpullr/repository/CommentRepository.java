package com.devbliss.gpullr.repository;

import org.springframework.data.jpa.repository.Query;

import com.devbliss.gpullr.domain.Comment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

  List<Comment> findAll();

  @Query("select c from Comment c where c.pullRequest.id = ? and c.notifications is empty")
  List<Comment> findAllUnreferenced(int pullRequestId);
}
