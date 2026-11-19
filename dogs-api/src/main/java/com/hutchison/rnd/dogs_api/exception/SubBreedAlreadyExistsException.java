package com.hutchison.rnd.dogs_api.exception;

public class SubBreedAlreadyExistsException extends RuntimeException {
    public SubBreedAlreadyExistsException(String message) {
        super(message);
    }
}