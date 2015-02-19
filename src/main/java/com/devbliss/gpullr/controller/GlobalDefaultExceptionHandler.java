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
    String commentStackTrace = createCommentStackTrace(
        String.format("'%s' requires log in", request.getRequestURL().toString()), e);

    StringBuilder responseBody = new StringBuilder();
    responseBody.append("<h1>FORBIDDEN</h1>\n");
    responseBody.append(commentStackTrace);

    return new ResponseEntity<String>(responseBody.toString(), HttpStatus.FORBIDDEN);
  }

  private String createCommentStackTrace(String reason, Exception e) {
    StringBuilder buf = new StringBuilder();
    buf.append("<!--\n");
    buf.append(reason);
    buf.append('\n');

    for (StackTraceElement s : e.getStackTrace()) {
      buf.append(s.toString() + "\n");
    }
    buf.append("-->");
    return buf.toString();
  }

}
