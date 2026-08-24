package com.autocare.controller;

import com.autocare.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService =
                appointmentService;
    }

    @GetMapping
    public String getAllAppointments(
            Model model
    ) {

        model.addAttribute(
                "appointments",
                appointmentService
                        .getAllAppointments()
        );

        return "admin-appointments";
    }

    @PostMapping("/confirm/{id}")
    public String confirmAppointment(
            @PathVariable UUID id
    ) {

        appointmentService.confirmAppointment(
                id
        );

        return "redirect:/admin/appointments?confirmed";
    }
}