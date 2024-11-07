package com.projet.mycose.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends APIException{
        public UserNotFoundException() {
            super(HttpStatus.NOT_FOUND,"Utilisateur not found");

        }
}
