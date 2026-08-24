package com.autocare.controller;

import com.autocare.service.ServiceOfferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceOfferController {

    private final ServiceOfferService serviceOfferService;

    public ServiceOfferController(
            ServiceOfferService serviceOfferService
    ) {
        this.serviceOfferService =
                serviceOfferService;
    }

    @GetMapping("/services")
    public String getServices(
            Model model
    ) {

        model.addAttribute(
                "services",
                serviceOfferService.getActiveServices()
        );

        return "services";
    }
}