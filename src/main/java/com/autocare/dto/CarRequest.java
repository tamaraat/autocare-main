package com.autocare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CarRequest {

    @NotBlank(message = "Make is required")
    @Size(
            min = 2,
            max = 30,
            message = "Make must be between 2 and 30 characters"
    )
    private String make;

    @NotBlank(message = "Model is required")
    @Size(
            min = 1,
            max = 50,
            message = "Model must be between 1 and 50 characters"
    )
    private String model;

    @NotBlank(message = "Registration number is required")
    @Size(
            min = 4,
            max = 15,
            message = "Registration number must be between 4 and 15 characters"
    )
    private String registrationNumber;

    @NotNull(message = "Production year is required")
    @Min(
            value = 1886,
            message = "Production year is invalid"
    )
    @Max(
            value = 2100,
            message = "Production year is invalid"
    )
    private Integer productionYear;

    public CarRequest() {
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }
}