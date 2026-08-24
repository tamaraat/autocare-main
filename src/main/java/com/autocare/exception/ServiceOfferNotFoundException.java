package com.autocare.exception;

public class ServiceOfferNotFoundException
        extends RuntimeException {

    public ServiceOfferNotFoundException(String message) {
        super(message);
    }
}