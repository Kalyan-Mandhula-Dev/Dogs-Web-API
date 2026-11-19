package com.hutchison.rnd.dogs_api.exception;

public class BreedAlreadyExistsException extends RuntimeException {
    public BreedAlreadyExistsException(String message) {
        super(message);
    }
}
