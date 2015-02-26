package com.devbliss.gpullr.controller.dto;

/**
 * DTO for JSON de-/serialization of errors in rest controllers.
 */
public class ErrorResponseDto {

  public ErrorResponseDto() {
  }

  public String errorKey;

  /**
   * the message is to be kept as short as possible
   */
  public String errorMessage;
}
