package com.devbliss.gpullr.repository;

import java.util.List;

import com.devbliss.gpullr.domain.Pullrequest;
import org.springframework.data.repository.CrudRepository;

public interface PullrequestRepository extends CrudRepository<Pullrequest, Integer>{

  List<Pullrequest> findAll();
}
