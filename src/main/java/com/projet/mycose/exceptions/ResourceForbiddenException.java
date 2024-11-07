package com.projet.mycose.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceForbiddenException extends APIException {
  public ResourceForbiddenException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }
}
