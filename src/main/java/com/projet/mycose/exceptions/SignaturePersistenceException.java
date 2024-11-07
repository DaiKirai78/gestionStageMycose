package com.projet.mycose.exceptions;

import java.io.IOException;

public class SignaturePersistenceException extends RuntimeException {
    public SignaturePersistenceException(String message) {
        super(message);
    }
}
