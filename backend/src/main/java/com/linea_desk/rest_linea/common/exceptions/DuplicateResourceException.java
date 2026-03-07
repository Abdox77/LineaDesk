package com.linea_desk.rest_linea.common.exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, String value) {
        super(resourceName + " with " + fieldName + " '" + value + "' already exists");
    }
}

