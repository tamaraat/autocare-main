package com.autocare.controller;

import com.autocare.dto.CarRequest;
import com.autocare.service.CarService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(
            CarService carService
    ) {
        this.carService = carService;
    }

    @GetMapping
    public String getMyCars(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "cars",
                carService.getCarsForUser(
                        authentication.getName()
                )
        );

        return "my-cars";
    }

    @GetMapping("/add")
    public String addCar(
            Model model
    ) {

        model.addAttribute(
                "carRequest",
                new CarRequest()
        );

        return "car-add";
    }

    @PostMapping("/add")
    public String addCar(
            @Valid
            @ModelAttribute("carRequest")
            CarRequest carRequest,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if (bindingResult.hasErrors()) {
            return "car-add";
        }

        try {

            carService.addCar(
                    carRequest,
                    authentication.getName()
            );

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "car.error",
                    exception.getMessage()
            );

            return "car-add";
        }

        return "redirect:/cars?added";
    }

    @GetMapping("/edit/{id}")
    public String editCar(
            @PathVariable UUID id,
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "carRequest",
                carService.getCarForEdit(
                        id,
                        authentication.getName()
                )
        );

        model.addAttribute(
                "carId",
                id
        );

        return "car-edit";
    }

    @PostMapping("/edit/{id}")
    public String editCar(
            @PathVariable UUID id,
            @Valid
            @ModelAttribute("carRequest")
            CarRequest carRequest,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "carId",
                    id
            );

            return "car-edit";
        }

        try {

            carService.editCar(
                    id,
                    carRequest,
                    authentication.getName()
            );

        } catch (IllegalArgumentException exception) {

            bindingResult.reject(
                    "car.error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "carId",
                    id
            );

            return "car-edit";
        }

        return "redirect:/cars?edited";
    }

    @PostMapping("/delete/{id}")
    public String deleteCar(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        carService.deleteCar(
                id,
                authentication.getName()
        );

        return "redirect:/cars?deleted";
    }
}