package com.devbliss.gpullr.exception;

/**
 * To be thrown when something unexpected happens.
 *
 * @author Henning Schütz <henning.schuetz@devbliss.com>
 */
public class UnexpectedException extends RuntimeException {

  private static final long serialVersionUID = -4487521264641991697L;

  public UnexpectedException(Throwable t) {
    super(t);
  }

  public UnexpectedException(String msg) {
    super(msg);
  }
}
