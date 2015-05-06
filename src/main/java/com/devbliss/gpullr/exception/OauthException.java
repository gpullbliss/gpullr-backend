package com.devbliss.gpullr.exception;

/**
 * To be thrown whenever a prerequisites for oauth communication
 * or oauth communication itself fails
 */
public class OauthException extends RuntimeException {

  private static final long serialVersionUID = 3920151583160427779L;

  public OauthException(Throwable cause) {
    super(cause);
  }

  public OauthException(String message) {
    super(message);
  }

}
