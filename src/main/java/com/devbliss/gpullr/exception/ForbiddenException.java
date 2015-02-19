package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a resource is accessed and the user is not allowed to do so.
 */
public class ForbiddenException extends RuntimeException {

  public ForbiddenException(String message) {
    super(message);
  }

  public ForbiddenException(Throwable cause) {
    super(cause);
  }

  public ForbiddenException() {
  }
}
