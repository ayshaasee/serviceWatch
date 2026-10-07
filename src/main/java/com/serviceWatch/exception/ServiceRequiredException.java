package com.serviceWatch.exception;

public class ServiceRequiredException extends RuntimeException {

    public ServiceRequiredException(String message) {
        super(message);
    }
}