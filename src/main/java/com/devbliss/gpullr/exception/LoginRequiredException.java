package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a resource is accessed and the user is required to log in beforehand.
 */
public class LoginRequiredException extends RuntimeException {

  public LoginRequiredException(String message) {
    super(message);
  }

  public LoginRequiredException(Throwable cause) {
    super(cause);
  }

  public LoginRequiredException() {
  }
}
