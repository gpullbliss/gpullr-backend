package com.devbliss.gpullr.repository;

import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts {@link LocalDate} to {@link Date} and vice versa for mapping respective entity values in JPA.
 * 
 * @author Henning Schütz <henning.schuetz@devbliss.com>
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
