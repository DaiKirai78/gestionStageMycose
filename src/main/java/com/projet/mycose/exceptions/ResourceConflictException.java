package com.projet.mycose.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends APIException {
  public ResourceConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
