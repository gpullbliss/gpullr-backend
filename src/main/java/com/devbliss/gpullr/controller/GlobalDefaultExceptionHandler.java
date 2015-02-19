package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.exception.LoginRequiredException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * a global default exception handler
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

  @ExceptionHandler(value = LoginRequiredException.class)
  public ResponseEntity<String> defaultForbidden(HttpServletRequest request, Exception e) throws Exception {
    StringBuilder responseBody = new StringBuilder();
    responseBody.append("<h1>FORBIDDEN</h1>\n");
    responseBody.append(String.format("log in required for '%s'", request.getRequestURL().toString()));

    return new ResponseEntity<String>(responseBody.toString(), HttpStatus.FORBIDDEN);
  }

}
