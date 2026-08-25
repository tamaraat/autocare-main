package com.autocare.controller;

import com.autocare.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(
            UserService userService
    ) {
        this.userService =
                userService;
    }

    @GetMapping
    public String getUsers(
            Model model
    ) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin-users";
    }

    @PostMapping("/{id}/role")
    public String toggleRole(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        userService.toggleRole(
                id,
                authentication.getName()
        );

        return "redirect:/admin/users?roleChanged";
    }
}