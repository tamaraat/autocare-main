package com.autocare.service;

import com.autocare.dto.ProfileRequest;
import com.autocare.dto.RegisterRequest;
import com.autocare.entity.User;
import com.autocare.entity.UserRole;
import com.autocare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    UserService.class
            );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Transactional
    public void register(
            RegisterRequest registerRequest
    ) {

        if (userRepository.existsByUsername(
                registerRequest.getUsername()
        )) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (!registerRequest
                .getPassword()
                .equals(
                        registerRequest
                                .getConfirmPassword()
                )) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        User user = new User();

        user.setUsername(
                registerRequest
                        .getUsername()
                        .trim()
        );

        user.setFirstName(
                registerRequest
                        .getFirstName()
                        .trim()
        );

        user.setLastName(
                registerRequest
                        .getLastName()
                        .trim()
        );

        user.setPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        user.setRole(
                UserRole.CUSTOMER
        );

        userRepository.save(user);

        log.info(
                "Registered user {}",
                user.getUsername()
        );
    }

    public ProfileRequest getProfile(
            String username
    ) {

        User user =
                findByUsername(username);

        ProfileRequest request =
                new ProfileRequest();

        request.setFirstName(
                user.getFirstName()
        );

        request.setLastName(
                user.getLastName()
        );

        return request;
    }

    @Transactional
    public void updateProfile(
            String username,
            ProfileRequest request
    ) {

        User user =
                findByUsername(username);

        String firstName =
                request
                        .getFirstName()
                        .trim();

        String lastName =
                request
                        .getLastName()
                        .trim();

        validateName(
                firstName,
                "First name"
        );

        validateName(
                lastName,
                "Last name"
        );

        user.setFirstName(firstName);
        user.setLastName(lastName);

        userRepository.save(user);

        log.info(
                "Updated profile for user {}",
                username
        );
    }

    public UserRole getRole(
            String username
    ) {

        return findByUsername(
                username
        ).getRole();
    }

    public List<User> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getUsername,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }

    @Transactional
    public void toggleRole(
            UUID userId,
            String currentAdminUsername
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        if (user.getUsername()
                .equals(currentAdminUsername)) {

            throw new IllegalArgumentException(
                    "You cannot change your own role"
            );
        }

        if (user.getRole()
                == UserRole.ADMIN) {

            user.setRole(
                    UserRole.CUSTOMER
            );

        } else {

            user.setRole(
                    UserRole.ADMIN
            );
        }

        userRepository.save(user);

        log.info(
                "Changed role of user {} to {}",
                user.getUsername(),
                user.getRole()
        );
    }

    private User findByUsername(
            String username
    ) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private void validateName(
            String name,
            String fieldName
    ) {

        if (name.length() < 2
                || name.length() > 30) {

            throw new IllegalArgumentException(
                    fieldName
                            + " must be between 2 and 30 characters"
            );
        }
    }
}