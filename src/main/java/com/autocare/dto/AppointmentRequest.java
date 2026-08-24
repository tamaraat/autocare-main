package com.autocare.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentRequest {

    @NotNull(message = "Please select a car")
    private UUID carId;

    @NotNull(message = "Please select a service")
    private UUID serviceOfferId;

    @NotNull(message = "Please select date and time")
    @Future(message = "Appointment must be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentTime;

    public AppointmentRequest() {
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public UUID getServiceOfferId() {
        return serviceOfferId;
    }

    public void setServiceOfferId(UUID serviceOfferId) {
        this.serviceOfferId = serviceOfferId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}