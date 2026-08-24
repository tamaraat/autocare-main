package com.autocare.repository;

import com.autocare.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    List<Appointment>
    findAllByCarOwnerUsernameOrderByAppointmentTimeDesc(
            String username
    );

    Optional<Appointment>
    findByIdAndCarOwnerUsername(
            UUID id,
            String username
    );

    List<Appointment>
    findAllByOrderByAppointmentTimeDesc();
}