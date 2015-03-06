package com.devbliss.gpullr.repository;

import java.time.ZonedDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts {@link ZonedDateTime} to {@link String} and vice versa for mapping respective entity values in JPA.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, String> {

  @Override
  public String convertToDatabaseColumn(ZonedDateTime entityAttribute) {
    if(entityAttribute != null) {
      return entityAttribute.toString();
    } else {
      return null;
    }
  }

  @Override
  public ZonedDateTime convertToEntityAttribute(String dbData) {
    if (dbData != null) {
      return ZonedDateTime.parse(dbData);
    } else {
      return null;
    }
  }
}
