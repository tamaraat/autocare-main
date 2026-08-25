package com.autocare.repository;

import com.autocare.entity.Car;
import com.autocare.entity.User;
import com.autocare.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CarRepositoryIntegrationTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindCarForOwner() {

        String username =
                "tu_"
                        + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12);

        String registrationNumber =
                "T"
                        + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        User owner =
                new User();

        owner.setUsername(username);

        owner.setPassword(
                "encoded-test-password"
        );

        owner.setFirstName(
                "Test"
        );

        owner.setLastName(
                "User"
        );

        owner.setRole(
                UserRole.CUSTOMER
        );

        owner =
                userRepository.save(
                        owner
                );

        Car car =
                new Car();

        car.setMake(
                "Toyota"
        );

        car.setModel(
                "Corolla"
        );

        car.setRegistrationNumber(
                registrationNumber
        );

        car.setProductionYear(
                2021
        );

        car.setOwner(
                owner
        );

        Car savedCar =
                carRepository.save(
                        car
                );

        Optional<Car> found =
                carRepository
                        .findByIdAndOwnerUsername(
                                savedCar.getId(),
                                username
                        );

        assertTrue(
                found.isPresent()
        );

        assertEquals(
                "Toyota",
                found.get().getMake()
        );

        assertEquals(
                registrationNumber,
                found.get()
                        .getRegistrationNumber()
        );

        List<Car> cars =
                carRepository
                        .findAllByOwnerUsernameOrderByMakeAscModelAsc(
                                username
                        );

        assertEquals(
                1,
                cars.size()
        );
    }
}