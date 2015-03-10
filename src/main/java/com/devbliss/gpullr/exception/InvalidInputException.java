package com.devbliss.gpullr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * To be thrown whenever a user or the frontend has sent invalid or incomplete data or requests.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidInputException extends RuntimeException {

  private static final long serialVersionUID = -1749257237661508754L;

  public InvalidInputException(String message) {
    super(message);
  }

  public InvalidInputException(Throwable cause) {
    super(cause);
  }
}
