package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a user or the frontend has sent invalid or incomplete data or requests.
 */
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = -1749257237661508754L;

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(Throwable cause) {
    super(cause);
  }
}
