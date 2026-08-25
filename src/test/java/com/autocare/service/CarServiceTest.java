package com.autocare.service;

import com.autocare.dto.CarRequest;
import com.autocare.entity.Car;
import com.autocare.entity.User;
import com.autocare.exception.CarNotFoundException;
import com.autocare.repository.CarRepository;
import com.autocare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    private CarService carService;

    @BeforeEach
    void setUp() {
        carService = new CarService(
                carRepository,
                userRepository
        );
    }

    @Test
    void addCarShouldNormalizeRegistrationNumberAndSaveCar() {

        User owner = new User();
        owner.setUsername("tamara");

        CarRequest request = createRequest(
                "BMW",
                "X5",
                " ca1234ab ",
                2020
        );

        when(
                carRepository
                        .existsByRegistrationNumberIgnoreCase(
                                "CA1234AB"
                        )
        ).thenReturn(false);

        when(
                userRepository
                        .findByUsername("tamara")
        ).thenReturn(
                Optional.of(owner)
        );

        carService.addCar(
                request,
                "tamara"
        );

        ArgumentCaptor<Car> carCaptor =
                ArgumentCaptor.forClass(
                        Car.class
                );

        verify(
                carRepository
        ).save(
                carCaptor.capture()
        );

        Car savedCar =
                carCaptor.getValue();

        assertEquals(
                "BMW",
                savedCar.getMake()
        );

        assertEquals(
                "X5",
                savedCar.getModel()
        );

        assertEquals(
                "CA1234AB",
                savedCar.getRegistrationNumber()
        );

        assertEquals(
                2020,
                savedCar.getProductionYear()
        );

        assertSame(
                owner,
                savedCar.getOwner()
        );
    }

    @Test
    void addCarShouldRejectDuplicateRegistrationNumber() {

        CarRequest request = createRequest(
                "Audi",
                "A4",
                "PB1234AB",
                2019
        );

        when(
                carRepository
                        .existsByRegistrationNumberIgnoreCase(
                                "PB1234AB"
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                carService.addCar(
                                        request,
                                        "tamara"
                                )
                );

        assertEquals(
                "A car with this registration number already exists",
                exception.getMessage()
        );

        verify(
                carRepository,
                never()
        ).save(
                any(Car.class)
        );
    }

    @Test
    void editCarShouldUpdateOwnedCar() {

        UUID carId =
                UUID.randomUUID();

        Car car = new Car();

        car.setId(carId);
        car.setMake("BMW");
        car.setModel("X3");
        car.setRegistrationNumber(
                "CA1111AA"
        );
        car.setProductionYear(2018);

        CarRequest request = createRequest(
                "Mercedes",
                "GLE",
                "ca2222bb",
                2022
        );

        when(
                carRepository
                        .findByIdAndOwnerUsername(
                                carId,
                                "tamara"
                        )
        ).thenReturn(
                Optional.of(car)
        );

        when(
                carRepository
                        .existsByRegistrationNumberIgnoreCaseAndIdNot(
                                "CA2222BB",
                                carId
                        )
        ).thenReturn(false);

        carService.editCar(
                carId,
                request,
                "tamara"
        );

        assertEquals(
                "Mercedes",
                car.getMake()
        );

        assertEquals(
                "GLE",
                car.getModel()
        );

        assertEquals(
                "CA2222BB",
                car.getRegistrationNumber()
        );

        assertEquals(
                2022,
                car.getProductionYear()
        );

        verify(
                carRepository
        ).save(car);
    }

    @Test
    void deleteCarShouldThrowWhenCarDoesNotBelongToUser() {

        UUID carId =
                UUID.randomUUID();

        when(
                carRepository
                        .findByIdAndOwnerUsername(
                                carId,
                                "tamara"
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                CarNotFoundException.class,
                () ->
                        carService.deleteCar(
                                carId,
                                "tamara"
                        )
        );

        verify(
                carRepository,
                never()
        ).delete(
                any(Car.class)
        );
    }

    private CarRequest createRequest(
            String make,
            String model,
            String registrationNumber,
            Integer productionYear
    ) {

        CarRequest request =
                new CarRequest();

        request.setMake(make);
        request.setModel(model);
        request.setRegistrationNumber(
                registrationNumber
        );
        request.setProductionYear(
                productionYear
        );

        return request;
    }
}