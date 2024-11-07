package com.projet.mycose.exceptions;

import java.io.IOException;

public class SignaturePersistenceException extends ResourcePersistenceException {
    public SignaturePersistenceException(String message) {
        super(message);
    }
}
