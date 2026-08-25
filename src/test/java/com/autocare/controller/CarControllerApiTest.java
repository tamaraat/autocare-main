package com.autocare.controller;

import com.autocare.dto.CarRequest;
import com.autocare.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarControllerApiTest {

    private CarService carService;

    private MockMvc mockMvc;

    private Principal principal;

    @BeforeEach
    void setUp() {

        carService =
                mock(CarService.class);

        CarController controller =
                new CarController(
                        carService
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                controller
                        )
                        .build();

        principal =
                new UsernamePasswordAuthenticationToken(
                        "tamara",
                        "password"
                );
    }

    @Test
    void getCarsShouldReturnMyCarsView()
            throws Exception {

        when(
                carService
                        .getCarsForUser(
                                "tamara"
                        )
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get("/cars")
                                .principal(
                                        principal
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "my-cars"
                        )
                )
                .andExpect(
                        model()
                                .attributeExists(
                                        "cars"
                                )
                );

        verify(
                carService
        ).getCarsForUser(
                "tamara"
        );
    }

    @Test
    void addCarShouldRedirectAfterSuccessfulCreation()
            throws Exception {

        mockMvc.perform(
                        post("/cars/add")
                                .principal(
                                        principal
                                )
                                .param(
                                        "make",
                                        "BMW"
                                )
                                .param(
                                        "model",
                                        "X5"
                                )
                                .param(
                                        "registrationNumber",
                                        "CA1234AB"
                                )
                                .param(
                                        "productionYear",
                                        "2020"
                                )
                )
                .andExpect(
                        status()
                                .is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/cars?added"
                        )
                );

        verify(
                carService
        ).addCar(
                any(CarRequest.class),
                eq("tamara")
        );
    }

    @Test
    void invalidAddCarRequestShouldReturnForm()
            throws Exception {

        mockMvc.perform(
                        post("/cars/add")
                                .principal(
                                        principal
                                )
                                .param(
                                        "make",
                                        ""
                                )
                                .param(
                                        "model",
                                        ""
                                )
                                .param(
                                        "registrationNumber",
                                        ""
                                )
                                .param(
                                        "productionYear",
                                        "1800"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(
                                "car-add"
                        )
                )
                .andExpect(
                        model()
                                .attributeHasErrors(
                                        "carRequest"
                                )
                );

        verify(
                carService,
                never()
        ).addCar(
                any(CarRequest.class),
                anyString()
        );
    }

    @Test
    void deleteCarShouldCallServiceAndRedirect()
            throws Exception {

        UUID carId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/cars/delete/{id}",
                                carId
                        )
                                .principal(
                                        principal
                                )
                )
                .andExpect(
                        status()
                                .is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/cars?deleted"
                        )
                );

        verify(
                carService
        ).deleteCar(
                carId,
                "tamara"
        );
    }
}