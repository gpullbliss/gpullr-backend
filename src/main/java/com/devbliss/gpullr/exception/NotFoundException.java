package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a resource is unavailable.
 */
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 5429545671149022729L;

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }
}
