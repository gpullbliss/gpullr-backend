package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a resource is accessed and the user is required to log in beforehand.
 */
public class LoginRequiredException extends RuntimeException {

  private static final long serialVersionUID = -3356763029053744330L;

  public LoginRequiredException(String message) {
    super(message);
  }

  public LoginRequiredException(Throwable cause) {
    super(cause);
  }

  public LoginRequiredException() {
  }
}
