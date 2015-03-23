package com.devbliss.gpullr.controller;

import com.devbliss.gpullr.controller.dto.ErrorResponseDto;
import com.devbliss.gpullr.exception.BadRequestException;
import com.devbliss.gpullr.exception.LoginRequiredException;
import com.devbliss.gpullr.exception.NotFoundException;
import com.devbliss.gpullr.exception.UnexpectedException;
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

  @ExceptionHandler(LoginRequiredException.class)
  public ResponseEntity<ErrorResponseDto> defaultForbidden(HttpServletRequest request, Exception e) throws Exception {
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.errorKey = Constants.KEY_DTO_ERROR_FORBIDDEN;
    errorResponseDto.errorMessage = String.format("'%s' requires log in", request.getRequestURL().toString());
    return new ResponseEntity<ErrorResponseDto>(errorResponseDto, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDto> notFound(NotFoundException e) {
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.errorKey = Constants.KEY_DTO_ERROR_NOT_FOUND;
    errorResponseDto.errorMessage = e.getMessage();
    return new ResponseEntity<ErrorResponseDto>(errorResponseDto, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnexpectedException.class)
  public ResponseEntity<ErrorResponseDto> unexpected() {
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.errorKey = Constants.KEY_DTO_ERROR_INTERNAL;
    errorResponseDto.errorMessage = "An unexpected server error occured.";
    return new ResponseEntity<ErrorResponseDto>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponseDto> badRequest(BadRequestException e) {
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.errorKey = Constants.KEY_DTO_ERROR_BAD_REQUEST;
    errorResponseDto.errorMessage = e.getMessage();
    return new ResponseEntity<ErrorResponseDto>(errorResponseDto, HttpStatus.BAD_REQUEST);
  }
}
