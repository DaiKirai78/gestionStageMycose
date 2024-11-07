package com.projet.mycose.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotAvailableException extends APIException {
  public ResourceNotAvailableException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
