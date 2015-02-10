package com.devbliss.gpullr.domain;

import java.time.LocalDate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;

/**
 * Stores the information whether / when this application has fetched all data from GitHub as data initialization. 
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 *
 */
@Entity
public class CacheAge {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  public LocalDate lastFullFetch;
}
