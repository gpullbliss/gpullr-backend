package com.devbliss.gpullr.service;

import com.devbliss.gpullr.domain.Comment;
import com.devbliss.gpullr.repository.CommentRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

  private final CommentRepository commentRepository;

  @Autowired
  public CommentService(
      CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public void save(List<Comment> pullRequestComments) {
    LOGGER.debug("saving {} pullrequestComments", pullRequestComments.size());
    commentRepository.save(pullRequestComments);
  }
}
