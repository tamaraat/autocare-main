package com.autocare.controller;

import com.autocare.dto.ServiceOfferRequest;
import com.autocare.service.ServiceOfferService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final ServiceOfferService serviceOfferService;

    public AdminServiceController(
            ServiceOfferService serviceOfferService
    ) {
        this.serviceOfferService =
                serviceOfferService;
    }

    @GetMapping
    public String getServices(
            Model model
    ) {

        model.addAttribute(
                "services",
                serviceOfferService.getAllServices()
        );

        return "admin-services";
    }

    @GetMapping("/add")
    public String addService(
            Model model
    ) {

        model.addAttribute(
                "serviceOfferRequest",
                new ServiceOfferRequest()
        );

        return "service-add";
    }

    @PostMapping("/add")
    public String addService(
            @Valid
            @ModelAttribute("serviceOfferRequest")
            ServiceOfferRequest request,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "service-add";
        }

        try {

            serviceOfferService.addService(
                    request
            );

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "service.error",
                    exception.getMessage()
            );

            return "service-add";
        }

        return "redirect:/admin/services?added";
    }

    @GetMapping("/edit/{id}")
    public String editService(
            @PathVariable UUID id,
            Model model
    ) {

        model.addAttribute(
                "serviceOfferRequest",
                serviceOfferService
                        .getServiceForEdit(id)
        );

        model.addAttribute(
                "serviceId",
                id
        );

        return "service-edit";
    }

    @PostMapping("/edit/{id}")
    public String editService(
            @PathVariable UUID id,
            @Valid
            @ModelAttribute("serviceOfferRequest")
            ServiceOfferRequest request,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "serviceId",
                    id
            );

            return "service-edit";
        }

        try {

            serviceOfferService.editService(
                    id,
                    request
            );

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "service.error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "serviceId",
                    id
            );

            return "service-edit";
        }

        return "redirect:/admin/services?edited";
    }

    @PostMapping("/toggle/{id}")
    public String toggleService(
            @PathVariable UUID id
    ) {

        serviceOfferService.toggleService(
                id
        );

        return "redirect:/admin/services?statusChanged";
    }
}