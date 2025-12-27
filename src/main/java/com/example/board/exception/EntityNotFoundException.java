package com.example.board.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object identifier) {
        super(entityName + " not found with identifier: " + identifier);
    }
}