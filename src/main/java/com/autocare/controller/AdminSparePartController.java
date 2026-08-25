package com.autocare.controller;

import com.autocare.dto.SparePartRequest;
import com.autocare.dto.StockUpdateRequest;
import com.autocare.service.SparePartIntegrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/parts")
public class AdminSparePartController {

    private final SparePartIntegrationService sparePartService;

    public AdminSparePartController(
            SparePartIntegrationService sparePartService
    ) {
        this.sparePartService =
                sparePartService;
    }

    @GetMapping
    public String getAllParts(
            Model model
    ) {

        model.addAttribute(
                "parts",
                sparePartService.getAllParts()
        );

        return "admin-parts";
    }

    @GetMapping("/add")
    public String addPart(
            Model model
    ) {

        model.addAttribute(
                "sparePartRequest",
                new SparePartRequest()
        );

        return "part-add";
    }

    @PostMapping("/add")
    public String addPart(
            @Valid
            @ModelAttribute("sparePartRequest")
            SparePartRequest request,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "part-add";
        }

        sparePartService.addPart(
                request
        );

        return "redirect:/admin/parts?added";
    }

    @GetMapping("/edit/{id}")
    public String editPart(
            @PathVariable UUID id,
            Model model
    ) {

        model.addAttribute(
                "sparePartRequest",
                sparePartService.getPartForEdit(id)
        );

        model.addAttribute(
                "partId",
                id
        );

        return "part-edit";
    }

    @PostMapping("/edit/{id}")
    public String editPart(
            @PathVariable UUID id,
            @Valid
            @ModelAttribute("sparePartRequest")
            SparePartRequest request,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "partId",
                    id
            );

            return "part-edit";
        }

        sparePartService.updatePart(
                id,
                request
        );

        return "redirect:/admin/parts?edited";
    }

    @GetMapping("/stock/{id}")
    public String updateStock(
            @PathVariable UUID id,
            Model model
    ) {

        model.addAttribute(
                "stockUpdateRequest",
                new StockUpdateRequest()
        );

        model.addAttribute(
                "partId",
                id
        );

        return "part-stock";
    }

    @PostMapping("/stock/{id}")
    public String updateStock(
            @PathVariable UUID id,
            @Valid
            @ModelAttribute("stockUpdateRequest")
            StockUpdateRequest request,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "partId",
                    id
            );

            return "part-stock";
        }

        sparePartService.updateStock(
                id,
                request
        );

        return "redirect:/admin/parts?stockUpdated";
    }

    @PostMapping("/delete/{id}")
    public String deletePart(
            @PathVariable UUID id
    ) {

        sparePartService.deletePart(
                id
        );

        return "redirect:/admin/parts?deleted";
    }
}