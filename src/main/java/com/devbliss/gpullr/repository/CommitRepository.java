package com.devbliss.gpullr.repository;

import com.devbliss.gpullr.domain.Commit;
import org.springframework.data.repository.CrudRepository;

/**
 * Handles persistence pf {@link Commit} objects.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
public interface CommitRepository extends CrudRepository<Commit, String> {

}
