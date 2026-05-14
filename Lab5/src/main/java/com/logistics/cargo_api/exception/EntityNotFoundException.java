package com.logistics.cargo_api.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s з ID=%d не знайдено в базі даних", entityName, id));
    }
}
