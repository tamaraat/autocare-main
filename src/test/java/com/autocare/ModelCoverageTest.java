package com.autocare;

import com.autocare.dto.*;
import com.autocare.entity.*;
import com.autocare.exception.AppointmentNotFoundException;
import com.autocare.exception.CarNotFoundException;
import com.autocare.exception.ServiceOfferNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTest {

    @Test
    void shouldCoverEntitiesDtosAndExceptions() {

        UUID id = UUID.randomUUID();

        User user = new User();
        user.setId(id);
        user.setUsername("tamara");
        user.setPassword("encoded");
        user.setFirstName("Tamara");
        user.setLastName("Toshkova");
        user.setRole(UserRole.ADMIN);

        assertEquals(id, user.getId());
        assertEquals("tamara", user.getUsername());
        assertEquals("encoded", user.getPassword());
        assertEquals("Tamara", user.getFirstName());
        assertEquals("Toshkova", user.getLastName());
        assertEquals(UserRole.ADMIN, user.getRole());

        Car car = new Car();
        car.setId(id);
        car.setMake("BMW");
        car.setModel("X5");
        car.setRegistrationNumber("CA1234AB");
        car.setProductionYear(2020);
        car.setOwner(user);

        assertEquals(id, car.getId());
        assertEquals("BMW", car.getMake());
        assertEquals("X5", car.getModel());
        assertEquals(
                "CA1234AB",
                car.getRegistrationNumber()
        );
        assertEquals(
                2020,
                car.getProductionYear()
        );
        assertSame(user, car.getOwner());

        ServiceOffer offer =
                new ServiceOffer();

        offer.setId(id);
        offer.setName("Oil Change");
        offer.setPrice(
                new BigDecimal("80")
        );
        offer.setDurationMinutes(60);
        offer.setActive(true);

        assertEquals(id, offer.getId());
        assertEquals(
                "Oil Change",
                offer.getName()
        );
        assertEquals(
                new BigDecimal("80"),
                offer.getPrice()
        );
        assertEquals(
                60,
                offer.getDurationMinutes()
        );
        assertTrue(offer.isActive());

        Appointment appointment =
                new Appointment();

        LocalDateTime time =
                LocalDateTime.now()
                        .plusDays(1);

        appointment.setId(id);
        appointment.setCar(car);
        appointment.setServiceOffer(offer);
        appointment.setAppointmentTime(time);
        appointment.setStatus(
                AppointmentStatus.PENDING
        );

        assertEquals(id, appointment.getId());
        assertSame(car, appointment.getCar());
        assertSame(
                offer,
                appointment.getServiceOffer()
        );
        assertEquals(
                time,
                appointment.getAppointmentTime()
        );
        assertEquals(
                AppointmentStatus.PENDING,
                appointment.getStatus()
        );

        CarRequest carRequest =
                new CarRequest();

        carRequest.setMake("Audi");
        carRequest.setModel("A4");
        carRequest.setRegistrationNumber(
                "PB1234AB"
        );
        carRequest.setProductionYear(2021);

        assertEquals(
                "Audi",
                carRequest.getMake()
        );
        assertEquals(
                "A4",
                carRequest.getModel()
        );
        assertEquals(
                "PB1234AB",
                carRequest.getRegistrationNumber()
        );
        assertEquals(
                2021,
                carRequest.getProductionYear()
        );

        RegisterRequest register =
                new RegisterRequest();

        register.setUsername("testuser");
        register.setFirstName("Test");
        register.setLastName("User");
        register.setPassword("secret");
        register.setConfirmPassword(
                "secret"
        );

        assertEquals(
                "testuser",
                register.getUsername()
        );
        assertEquals(
                "Test",
                register.getFirstName()
        );
        assertEquals(
                "User",
                register.getLastName()
        );
        assertEquals(
                "secret",
                register.getPassword()
        );
        assertEquals(
                "secret",
                register.getConfirmPassword()
        );

        ProfileRequest profile =
                new ProfileRequest();

        profile.setFirstName("Test");
        profile.setLastName("User");

        assertEquals(
                "Test",
                profile.getFirstName()
        );
        assertEquals(
                "User",
                profile.getLastName()
        );

        AppointmentRequest appointmentRequest =
                new AppointmentRequest();

        appointmentRequest.setCarId(id);
        appointmentRequest.setServiceOfferId(id);
        appointmentRequest.setAppointmentTime(
                time
        );

        assertEquals(
                id,
                appointmentRequest.getCarId()
        );
        assertEquals(
                id,
                appointmentRequest
                        .getServiceOfferId()
        );
        assertEquals(
                time,
                appointmentRequest
                        .getAppointmentTime()
        );

        ServiceOfferRequest serviceRequest =
                new ServiceOfferRequest();

        serviceRequest.setName("Repair");
        serviceRequest.setPrice(
                new BigDecimal("120")
        );
        serviceRequest.setDurationMinutes(90);

        assertEquals(
                "Repair",
                serviceRequest.getName()
        );
        assertEquals(
                new BigDecimal("120"),
                serviceRequest.getPrice()
        );
        assertEquals(
                90,
                serviceRequest
                        .getDurationMinutes()
        );

        SparePartRequest partRequest =
                new SparePartRequest();

        partRequest.setName("Filter");
        partRequest.setPartNumber("F-1");
        partRequest.setQuantity(5);
        partRequest.setPrice(
                new BigDecimal("20")
        );

        assertEquals(
                "Filter",
                partRequest.getName()
        );
        assertEquals(
                "F-1",
                partRequest.getPartNumber()
        );
        assertEquals(
                5,
                partRequest.getQuantity()
        );
        assertEquals(
                new BigDecimal("20"),
                partRequest.getPrice()
        );

        SparePartResponse partResponse =
                new SparePartResponse();

        partResponse.setId(id);
        partResponse.setName("Filter");
        partResponse.setPartNumber("F-1");
        partResponse.setQuantity(5);
        partResponse.setPrice(
                new BigDecimal("20")
        );

        assertEquals(id, partResponse.getId());
        assertEquals(
                "Filter",
                partResponse.getName()
        );
        assertEquals(
                "F-1",
                partResponse.getPartNumber()
        );
        assertEquals(
                5,
                partResponse.getQuantity()
        );
        assertEquals(
                new BigDecimal("20"),
                partResponse.getPrice()
        );

        StockUpdateRequest stock =
                new StockUpdateRequest();

        stock.setQuantity(12);

        assertEquals(
                12,
                stock.getQuantity()
        );

        assertEquals(
                "car error",
                new CarNotFoundException(
                        "car error"
                ).getMessage()
        );

        assertEquals(
                "service error",
                new ServiceOfferNotFoundException(
                        "service error"
                ).getMessage()
        );

        assertEquals(
                "appointment error",
                new AppointmentNotFoundException(
                        "appointment error"
                ).getMessage()
        );
    }
}