package com.autocare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceOfferRequest {

    @NotBlank(message = "Service name is required")
    @Size(
            min = 2,
            max = 60,
            message = "Service name must be between 2 and 60 characters"
    )
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0"
    )
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Min(
            value = 10,
            message = "Duration must be at least 10 minutes"
    )
    private Integer durationMinutes;

    public ServiceOfferRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}