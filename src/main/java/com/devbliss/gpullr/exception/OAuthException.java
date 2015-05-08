package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a prerequisites for OAuth communication
 * or OAuth communication itself fails
 */
public class OAuthException extends RuntimeException {

  private static final long serialVersionUID = 3920151583160427779L;

  public OAuthException(Throwable cause) {
    super(cause);
  }

  public OAuthException(String message) {
    super(message);
  }

}
