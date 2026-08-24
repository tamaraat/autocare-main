package com.autocare.controller;

import com.autocare.dto.AppointmentRequest;
import com.autocare.service.AppointmentService;
import com.autocare.service.CarService;
import com.autocare.service.ServiceOfferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CarService carService;
    private final ServiceOfferService serviceOfferService;

    public AppointmentController(
            AppointmentService appointmentService,
            CarService carService,
            ServiceOfferService serviceOfferService
    ) {
        this.appointmentService =
                appointmentService;

        this.carService =
                carService;

        this.serviceOfferService =
                serviceOfferService;
    }

    @GetMapping
    public String getMyAppointments(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "appointments",
                appointmentService
                        .getAppointmentsForUser(
                                authentication.getName()
                        )
        );

        return "my-appointments";
    }

    @GetMapping("/add")
    public String addAppointment(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "appointmentRequest",
                new AppointmentRequest()
        );

        populateFormData(
                authentication,
                model
        );

        return "appointment-add";
    }

    @PostMapping("/add")
    public String addAppointment(
            @Valid
            @ModelAttribute("appointmentRequest")
            AppointmentRequest appointmentRequest,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            populateFormData(
                    authentication,
                    model
            );

            return "appointment-add";
        }

        try {

            appointmentService.bookAppointment(
                    appointmentRequest,
                    authentication.getName()
            );

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "appointment.error",
                    exception.getMessage()
            );

            populateFormData(
                    authentication,
                    model
            );

            return "appointment-add";
        }

        return "redirect:/appointments?booked";
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        appointmentService.cancelAppointment(
                id,
                authentication.getName()
        );

        return "redirect:/appointments?cancelled";
    }

    private void populateFormData(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "cars",
                carService.getCarsForUser(
                        authentication.getName()
                )
        );

        model.addAttribute(
                "services",
                serviceOfferService
                        .getActiveServices()
        );
    }
}