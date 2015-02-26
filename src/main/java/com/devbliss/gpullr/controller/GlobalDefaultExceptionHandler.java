package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ErrorResponseDto;
import com.devbliss.gpullr.exception.LoginRequiredException;
import com.devbliss.gpullr.util.Constants;
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
  public ResponseEntity<ErrorResponseDto> defaultForbidden(HttpServletRequest request, Exception e) throws Exception {
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.errorKey = Constants.KEY_DTO_ERROR_FORBIDDEN;
    errorResponseDto.errorMessage = String.format("'%s' requires log in", request.getRequestURL().toString());

    return new ResponseEntity<ErrorResponseDto>(errorResponseDto, HttpStatus.FORBIDDEN);
  }

}
