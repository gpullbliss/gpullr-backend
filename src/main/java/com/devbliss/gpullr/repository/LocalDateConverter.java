package com.devbliss.gpullr.repository;

import javax.persistence.Converter;

import javax.persistence.AttributeConverter;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Converts {@link LocalDate} to {@link Date} and vice versa for mapping respective entity values in JPA.
 * 
 * @author Henning Schütz <henning@byteshaper.com>
 *
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

  @Override
  public Date convertToDatabaseColumn(LocalDate entityAttibute) {
    return Date.valueOf(entityAttibute);
  }

  @Override
  public LocalDate convertToEntityAttribute(Date dbData) {
    return dbData.toLocalDate();
  }
}
