package com.autocare.controller;

import com.autocare.dto.ProfileRequest;
import com.autocare.dto.RegisterRequest;
import com.autocare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService =
                userService;
    }

    @GetMapping("/register")
    public String register(
            Model model
    ) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid
            @ModelAttribute("registerRequest")
            RegisterRequest registerRequest,
            BindingResult bindingResult
    ) {

        if (!registerRequest
                .getPassword()
                .equals(
                        registerRequest
                                .getConfirmPassword()
                )) {

            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Passwords do not match"
            );
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {

            userService.register(
                    registerRequest
            );

        } catch (
                IllegalArgumentException exception
        ) {

            bindingResult.reject(
                    "registration.error",
                    exception.getMessage()
            );

            return "register";
        }

        return "redirect:/users/login?registered";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model
    ) {

        String username =
                authentication.getName();

        model.addAttribute(
                "profileRequest",
                userService.getProfile(
                        username
                )
        );

        model.addAttribute(
                "username",
                username
        );

        model.addAttribute(
                "role",
                userService.getRole(
                        username
                )
        );

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid
            @ModelAttribute("profileRequest")
            ProfileRequest profileRequest,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {

        String username =
                authentication.getName();

        if (bindingResult.hasErrors()) {

            populateProfileInformation(
                    username,
                    model
            );

            return "profile";
        }

        try {

            userService.updateProfile(
                    username,
                    profileRequest
            );

        } catch (
                IllegalArgumentException exception
        ) {

            bindingResult.reject(
                    "profile.error",
                    exception.getMessage()
            );

            populateProfileInformation(
                    username,
                    model
            );

            return "profile";
        }

        return "redirect:/users/profile?updated";
    }

    private void populateProfileInformation(
            String username,
            Model model
    ) {

        model.addAttribute(
                "username",
                username
        );

        model.addAttribute(
                "role",
                userService.getRole(
                        username
                )
        );
    }
}