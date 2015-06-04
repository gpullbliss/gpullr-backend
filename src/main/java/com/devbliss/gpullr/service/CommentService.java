package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final CommentRepository commentRepository;

  @Autowired
  public CommentService(
      CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public void save(Comment comment) {
    commentRepository.save(comment);
  }
}
