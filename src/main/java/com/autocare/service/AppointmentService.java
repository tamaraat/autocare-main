package com.autocare.service;

import com.autocare.dto.AppointmentRequest;
import com.autocare.entity.Appointment;
import com.autocare.entity.AppointmentStatus;
import com.autocare.entity.Car;
import com.autocare.entity.ServiceOffer;
import com.autocare.exception.AppointmentNotFoundException;
import com.autocare.exception.CarNotFoundException;
import com.autocare.exception.ServiceOfferNotFoundException;
import com.autocare.repository.AppointmentRepository;
import com.autocare.repository.CarRepository;
import com.autocare.repository.ServiceOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AppointmentService.class
            );

    private final AppointmentRepository appointmentRepository;
    private final CarRepository carRepository;
    private final ServiceOfferRepository serviceOfferRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            CarRepository carRepository,
            ServiceOfferRepository serviceOfferRepository
    ) {
        this.appointmentRepository =
                appointmentRepository;

        this.carRepository =
                carRepository;

        this.serviceOfferRepository =
                serviceOfferRepository;
    }

    public List<Appointment> getAppointmentsForUser(
            String username
    ) {

        return appointmentRepository
                .findAllByCarOwnerUsernameOrderByAppointmentTimeDesc(
                        username
                );
    }

    public List<Appointment> getAllAppointments() {

        return appointmentRepository
                .findAllByOrderByAppointmentTimeDesc();
    }

    @Transactional
    public void bookAppointment(
            AppointmentRequest request,
            String username
    ) {

        validateAppointmentTime(
                request.getAppointmentTime()
        );

        Car car = carRepository
                .findByIdAndOwnerUsername(
                        request.getCarId(),
                        username
                )
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found or you do not have permission to use it"
                        )
                );

        ServiceOffer serviceOffer =
                serviceOfferRepository
                        .findById(
                                request.getServiceOfferId()
                        )
                        .orElseThrow(() ->
                                new ServiceOfferNotFoundException(
                                        "Service offer not found"
                                )
                        );

        if (!serviceOffer.isActive()) {

            throw new IllegalArgumentException(
                    "This service is currently unavailable"
            );
        }

        Appointment appointment =
                new Appointment();

        appointment.setCar(car);

        appointment.setServiceOffer(
                serviceOffer
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setStatus(
                AppointmentStatus.PENDING
        );

        appointmentRepository.save(
                appointment
        );

        log.info(
                "Created appointment with id {} for user {}",
                appointment.getId(),
                username
        );
    }

    @Transactional
    public void cancelAppointment(
            UUID appointmentId,
            String username
    ) {

        Appointment appointment =
                appointmentRepository
                        .findByIdAndCarOwnerUsername(
                                appointmentId,
                                username
                        )
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found or you do not have permission to access it"
                                )
                        );

        if (appointment.getStatus()
                == AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "A completed appointment cannot be cancelled"
            );
        }

        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new IllegalArgumentException(
                    "This appointment is already cancelled"
            );
        }

        appointment.setStatus(
                AppointmentStatus.CANCELLED
        );

        appointmentRepository.save(
                appointment
        );

        log.info(
                "Cancelled appointment with id {} by user {}",
                appointmentId,
                username
        );
    }

    @Transactional
    public void confirmAppointment(
            UUID appointmentId
    ) {

        Appointment appointment =
                findAppointment(
                        appointmentId
                );

        if (appointment.getStatus()
                != AppointmentStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only pending appointments can be confirmed"
            );
        }

        appointment.setStatus(
                AppointmentStatus.CONFIRMED
        );

        appointmentRepository.save(
                appointment
        );

        log.info(
                "Confirmed appointment with id {}",
                appointmentId
        );
    }

    private Appointment findAppointment(
            UUID appointmentId
    ) {

        return appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment not found"
                        )
                );
    }

    private void validateAppointmentTime(
            LocalDateTime appointmentTime
    ) {

        if (appointmentTime == null) {

            throw new IllegalArgumentException(
                    "Appointment date and time are required"
            );
        }

        if (!appointmentTime.isAfter(
                LocalDateTime.now()
        )) {

            throw new IllegalArgumentException(
                    "Appointment must be in the future"
            );
        }

        int hour =
                appointmentTime.getHour();

        if (hour < 8 || hour >= 18) {

            throw new IllegalArgumentException(
                    "Appointments can be booked between 08:00 and 18:00"
            );
        }
    }
}