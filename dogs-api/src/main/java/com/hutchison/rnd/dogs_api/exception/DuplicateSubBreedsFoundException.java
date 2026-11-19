package com.hutchison.rnd.dogs_api.exception;

public class DuplicateSubBreedsFoundException extends RuntimeException {
    public DuplicateSubBreedsFoundException(String message) {
        super(message);
    }
}
