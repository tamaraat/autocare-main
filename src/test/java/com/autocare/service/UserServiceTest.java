package com.autocare.service;

import com.autocare.dto.ProfileRequest;
import com.autocare.dto.RegisterRequest;
import com.autocare.entity.User;
import com.autocare.entity.UserRole;
import com.autocare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService =
                new UserService(
                        userRepository,
                        passwordEncoder
                );
    }

    @Test
    void registerShouldSaveUser() {

        RegisterRequest request =
                registerRequest();

        when(
                userRepository
                        .existsByUsername(
                                "tamara"
                        )
        ).thenReturn(false);

        when(
                passwordEncoder
                        .encode("secret1")
        ).thenReturn(
                "encoded"
        );

        userService.register(request);

        verify(userRepository)
                .save(
                        argThat(user ->
                                user.getUsername()
                                        .equals("tamara")
                                        &&
                                        user.getPassword()
                                                .equals("encoded")
                                        &&
                                        user.getRole()
                                                == UserRole.CUSTOMER
                        )
                );
    }

    @Test
    void registerDuplicateUsernameShouldFail() {

        RegisterRequest request =
                registerRequest();

        when(
                userRepository
                        .existsByUsername(
                                "tamara"
                        )
        ).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userService.register(request)
        );
    }

    @Test
    void registerDifferentPasswordsShouldFail() {

        RegisterRequest request =
                registerRequest();

        request.setConfirmPassword(
                "different"
        );

        when(
                userRepository
                        .existsByUsername(
                                "tamara"
                        )
        ).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userService.register(request)
        );
    }

    @Test
    void getProfileShouldReturnProfileData() {

        User user = createUser(
                "tamara",
                UserRole.CUSTOMER
        );

        when(
                userRepository
                        .findByUsername(
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        ProfileRequest profile =
                userService.getProfile(
                        "tamara"
                );

        assertEquals(
                "Tamara",
                profile.getFirstName()
        );

        assertEquals(
                "Toshkova",
                profile.getLastName()
        );
    }

    @Test
    void updateProfileShouldTrimAndSave() {

        User user = createUser(
                "tamara",
                UserRole.CUSTOMER
        );

        ProfileRequest request =
                new ProfileRequest();

        request.setFirstName(
                "  Maria "
        );

        request.setLastName(
                " Petrova "
        );

        when(
                userRepository
                        .findByUsername(
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        userService.updateProfile(
                "tamara",
                request
        );

        assertEquals(
                "Maria",
                user.getFirstName()
        );

        assertEquals(
                "Petrova",
                user.getLastName()
        );

        verify(
                userRepository
        ).save(user);
    }

    @Test
    void getRoleShouldReturnRole() {

        User user = createUser(
                "tamara",
                UserRole.ADMIN
        );

        when(
                userRepository
                        .findByUsername(
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        assertEquals(
                UserRole.ADMIN,
                userService.getRole(
                        "tamara"
                )
        );
    }

    @Test
    void getAllUsersShouldSortByUsername() {

        User zara = createUser(
                "zara",
                UserRole.CUSTOMER
        );

        User anna = createUser(
                "anna",
                UserRole.CUSTOMER
        );

        when(
                userRepository.findAll()
        ).thenReturn(
                List.of(zara, anna)
        );

        List<User> users =
                userService.getAllUsers();

        assertEquals(
                "anna",
                users.get(0).getUsername()
        );

        assertEquals(
                "zara",
                users.get(1).getUsername()
        );
    }

    @Test
    void toggleRoleShouldPromoteCustomer() {

        UUID id = UUID.randomUUID();

        User user = createUser(
                "otheruser",
                UserRole.CUSTOMER
        );

        user.setId(id);

        when(
                userRepository.findById(id)
        ).thenReturn(
                Optional.of(user)
        );

        userService.toggleRole(
                id,
                "tamara"
        );

        assertEquals(
                UserRole.ADMIN,
                user.getRole()
        );

        verify(
                userRepository
        ).save(user);
    }

    @Test
    void toggleOwnRoleShouldFail() {

        UUID id = UUID.randomUUID();

        User user = createUser(
                "tamara",
                UserRole.ADMIN
        );

        user.setId(id);

        when(
                userRepository.findById(id)
        ).thenReturn(
                Optional.of(user)
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        userService.toggleRole(
                                id,
                                "tamara"
                        )
        );
    }

    private RegisterRequest registerRequest() {

        RegisterRequest request =
                new RegisterRequest();

        request.setUsername("tamara");
        request.setFirstName("Tamara");
        request.setLastName("Toshkova");
        request.setPassword("secret1");
        request.setConfirmPassword(
                "secret1"
        );

        return request;
    }

    private User createUser(
            String username,
            UserRole role
    ) {

        User user = new User();

        user.setUsername(username);
        user.setPassword("encoded");
        user.setFirstName("Tamara");
        user.setLastName("Toshkova");
        user.setRole(role);

        return user;
    }
}