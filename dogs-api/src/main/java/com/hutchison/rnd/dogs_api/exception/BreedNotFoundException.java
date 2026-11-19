package com.hutchison.rnd.dogs_api.exception;

public class BreedNotFoundException extends RuntimeException {
    public BreedNotFoundException(String message) {
        super(message);
    }
}
