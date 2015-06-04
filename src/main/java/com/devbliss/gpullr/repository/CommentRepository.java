package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.Comment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

  List<Comment> findAll();
}
