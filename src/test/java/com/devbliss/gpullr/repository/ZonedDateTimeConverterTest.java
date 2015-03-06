package com.devbliss.gpullr.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.junit.Test;

public class ZonedDateTimeConverterTest {

  private ZonedDateTimeConverter zdtConverter;

  private static final String ZONED_DATE_TIME_STRING = "2015-04-18T18:15+02:00[Europe/Paris]";

  private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(
      LocalDateTime.of(2015, Month.APRIL, 18, 18, 15),
      ZoneId.of("Europe/Paris"));

  @Before
  public void setUp() {
    zdtConverter = new ZonedDateTimeConverter();
  }

  @Test
  public void convertToEntityAttributeSucceeds() {
    assertEquals(ZONED_DATE_TIME, zdtConverter.convertToEntityAttribute(ZONED_DATE_TIME_STRING));
  }

  @Test
  public void convertFromNullStringReturnsNull() {
    assertNull(zdtConverter.convertToEntityAttribute(null));
  }

  @Test
  public void convertToDatabaseColumnSucceeds() {
    assertEquals(ZONED_DATE_TIME_STRING, zdtConverter.convertToDatabaseColumn(ZONED_DATE_TIME));
  }
  
  @Test
  public void convertFromNullEntityReturnsNull() {
    assertNull(zdtConverter.convertToDatabaseColumn(null));
  }
}
