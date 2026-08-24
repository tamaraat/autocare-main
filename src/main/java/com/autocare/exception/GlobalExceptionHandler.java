package com.autocare.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarNotFoundException.class)
    public String handleCarNotFound(
            CarNotFoundException exception,
            Model model
    ) {

        model.addAttribute(
                "errorTitle",
                "Car Not Found"
        );

        model.addAttribute(
                "errorMessage",
                exception.getMessage()
        );

        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException exception,
            Model model
    ) {

        model.addAttribute(
                "errorTitle",
                "Invalid Operation"
        );

        model.addAttribute(
                "errorMessage",
                exception.getMessage()
        );

        return "error";
    }
    @ExceptionHandler(ServiceOfferNotFoundException.class)
    public String handleServiceOfferNotFound(
            ServiceOfferNotFoundException exception,
            Model model
    ) {

        model.addAttribute(
                "errorTitle",
                "Service Not Found"
        );

        model.addAttribute(
                "errorMessage",
                exception.getMessage()
        );

        return "error";
    }
}