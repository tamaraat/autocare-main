package com.autocare.service;

import com.autocare.dto.AppointmentRequest;
import com.autocare.entity.Appointment;
import com.autocare.entity.AppointmentStatus;
import com.autocare.entity.Car;
import com.autocare.entity.ServiceOffer;
import com.autocare.exception.AppointmentNotFoundException;
import com.autocare.repository.AppointmentRepository;
import com.autocare.repository.CarRepository;
import com.autocare.repository.ServiceOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ServiceOfferRepository serviceOfferRepository;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository,
                carRepository,
                serviceOfferRepository
        );
    }

    @Test
    void getAppointmentsForUserShouldReturnAppointments() {

        when(
                appointmentRepository
                        .findAllByCarOwnerUsernameOrderByAppointmentTimeDesc(
                                "tamara"
                        )
        ).thenReturn(List.of(new Appointment()));

        assertEquals(
                1,
                appointmentService
                        .getAppointmentsForUser("tamara")
                        .size()
        );
    }

    @Test
    void getAllAppointmentsShouldReturnAppointments() {

        when(
                appointmentRepository
                        .findAllByOrderByAppointmentTimeDesc()
        ).thenReturn(List.of(new Appointment()));

        assertEquals(
                1,
                appointmentService
                        .getAllAppointments()
                        .size()
        );
    }

    @Test
    void bookAppointmentShouldCreatePendingAppointment() {

        UUID carId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Car car = new Car();
        car.setId(carId);

        ServiceOffer serviceOffer =
                new ServiceOffer();

        serviceOffer.setId(serviceId);
        serviceOffer.setName("Oil Change");
        serviceOffer.setPrice(
                new BigDecimal("80.00")
        );
        serviceOffer.setDurationMinutes(60);
        serviceOffer.setActive(true);

        AppointmentRequest request =
                new AppointmentRequest();

        request.setCarId(carId);
        request.setServiceOfferId(serviceId);
        request.setAppointmentTime(
                validFutureTime()
        );

        when(
                carRepository
                        .findByIdAndOwnerUsername(
                                carId,
                                "tamara"
                        )
        ).thenReturn(Optional.of(car));

        when(
                serviceOfferRepository
                        .findById(serviceId)
        ).thenReturn(
                Optional.of(serviceOffer)
        );

        appointmentService.bookAppointment(
                request,
                "tamara"
        );

        ArgumentCaptor<Appointment> captor =
                ArgumentCaptor.forClass(
                        Appointment.class
                );

        verify(
                appointmentRepository
        ).save(captor.capture());

        Appointment appointment =
                captor.getValue();

        assertSame(
                car,
                appointment.getCar()
        );

        assertSame(
                serviceOffer,
                appointment.getServiceOffer()
        );

        assertEquals(
                AppointmentStatus.PENDING,
                appointment.getStatus()
        );

        assertEquals(
                request.getAppointmentTime(),
                appointment.getAppointmentTime()
        );
    }

    @Test
    void bookAppointmentShouldRejectInactiveService() {

        UUID carId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        AppointmentRequest request =
                new AppointmentRequest();

        request.setCarId(carId);
        request.setServiceOfferId(serviceId);
        request.setAppointmentTime(
                validFutureTime()
        );

        Car car = new Car();

        ServiceOffer serviceOffer =
                new ServiceOffer();

        serviceOffer.setActive(false);

        when(
                carRepository
                        .findByIdAndOwnerUsername(
                                carId,
                                "tamara"
                        )
        ).thenReturn(Optional.of(car));

        when(
                serviceOfferRepository
                        .findById(serviceId)
        ).thenReturn(
                Optional.of(serviceOffer)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        appointmentService
                                .bookAppointment(
                                        request,
                                        "tamara"
                                )
        );

        verify(
                appointmentRepository,
                never()
        ).save(any());
    }

    @Test
    void bookAppointmentShouldRejectPastDate() {

        AppointmentRequest request =
                new AppointmentRequest();

        request.setAppointmentTime(
                LocalDateTime.now()
                        .minusDays(1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        appointmentService
                                .bookAppointment(
                                        request,
                                        "tamara"
                                )
        );
    }

    @Test
    void cancelAppointmentShouldCancelPendingAppointment() {

        UUID id = UUID.randomUUID();

        Appointment appointment =
                new Appointment();

        appointment.setStatus(
                AppointmentStatus.PENDING
        );

        when(
                appointmentRepository
                        .findByIdAndCarOwnerUsername(
                                id,
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        appointmentService.cancelAppointment(
                id,
                "tamara"
        );

        assertEquals(
                AppointmentStatus.CANCELLED,
                appointment.getStatus()
        );

        verify(
                appointmentRepository
        ).save(appointment);
    }

    @Test
    void cancelCompletedAppointmentShouldFail() {

        UUID id = UUID.randomUUID();

        Appointment appointment =
                new Appointment();

        appointment.setStatus(
                AppointmentStatus.COMPLETED
        );

        when(
                appointmentRepository
                        .findByIdAndCarOwnerUsername(
                                id,
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        appointmentService
                                .cancelAppointment(
                                        id,
                                        "tamara"
                                )
        );
    }

    @Test
    void confirmAppointmentShouldSetConfirmed() {

        UUID id = UUID.randomUUID();

        Appointment appointment =
                new Appointment();

        appointment.setStatus(
                AppointmentStatus.PENDING
        );

        when(
                appointmentRepository
                        .findById(id)
        ).thenReturn(
                Optional.of(appointment)
        );

        appointmentService
                .confirmAppointment(id);

        assertEquals(
                AppointmentStatus.CONFIRMED,
                appointment.getStatus()
        );

        verify(
                appointmentRepository
        ).save(appointment);
    }

    @Test
    void confirmNonPendingAppointmentShouldFail() {

        UUID id = UUID.randomUUID();

        Appointment appointment =
                new Appointment();

        appointment.setStatus(
                AppointmentStatus.CANCELLED
        );

        when(
                appointmentRepository
                        .findById(id)
        ).thenReturn(
                Optional.of(appointment)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        appointmentService
                                .confirmAppointment(id)
        );
    }

    @Test
    void confirmMissingAppointmentShouldFail() {

        UUID id = UUID.randomUUID();

        when(
                appointmentRepository
                        .findById(id)
        ).thenReturn(Optional.empty());

        assertThrows(
                AppointmentNotFoundException.class,
                () ->
                        appointmentService
                                .confirmAppointment(id)
        );
    }

    private LocalDateTime validFutureTime() {

        return LocalDateTime.now()
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}