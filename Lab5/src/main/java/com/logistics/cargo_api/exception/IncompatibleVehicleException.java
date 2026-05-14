package com.logistics.cargo_api.exception;

public class IncompatibleVehicleException extends RuntimeException {
    public IncompatibleVehicleException(String message) {
        super(message);
    }
}