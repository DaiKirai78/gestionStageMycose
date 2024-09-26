package com.projet.mycose.security.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends APIException {
  public InvalidJwtTokenException(HttpStatus status, String message) {
    super(status, message);
  }
}
