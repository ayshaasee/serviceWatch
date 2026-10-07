package com.serviceWatch.exception;

public class InvalidIncidentStatusException extends RuntimeException {

    public InvalidIncidentStatusException(String message) {
        super(message);
    }
}