package com.logistics.cargo_api.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super(String.format("Неможливо змінити статус з '%s' на '%s'", from, to));
    }

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}