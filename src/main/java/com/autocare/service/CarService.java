package com.autocare.service;

import com.autocare.dto.CarRequest;
import com.autocare.entity.Car;
import com.autocare.entity.User;
import com.autocare.exception.CarNotFoundException;
import com.autocare.repository.CarRepository;
import com.autocare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    private static final Logger log =
            LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarService(
            CarRepository carRepository,
            UserRepository userRepository
    ) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    public List<Car> getCarsForUser(String username) {

        return carRepository
                .findAllByOwnerUsernameOrderByMakeAscModelAsc(username);
    }

    @Transactional
    public void addCar(
            CarRequest carRequest,
            String username
    ) {

        validateProductionYear(
                carRequest.getProductionYear()
        );

        String registrationNumber =
                normalizeRegistrationNumber(
                        carRequest.getRegistrationNumber()
                );

        if (carRepository
                .existsByRegistrationNumberIgnoreCase(
                        registrationNumber
                )) {

            throw new IllegalArgumentException(
                    "A car with this registration number already exists"
            );
        }

        User owner = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        Car car = new Car();

        car.setMake(
                carRequest.getMake().trim()
        );

        car.setModel(
                carRequest.getModel().trim()
        );

        car.setRegistrationNumber(
                registrationNumber
        );

        car.setProductionYear(
                carRequest.getProductionYear()
        );

        car.setOwner(owner);

        carRepository.save(car);

        log.info(
                "Created car with id {} for user {}",
                car.getId(),
                username
        );
    }

    public CarRequest getCarForEdit(
            UUID carId,
            String username
    ) {

        Car car = findOwnedCar(
                carId,
                username
        );

        CarRequest carRequest =
                new CarRequest();

        carRequest.setMake(
                car.getMake()
        );

        carRequest.setModel(
                car.getModel()
        );

        carRequest.setRegistrationNumber(
                car.getRegistrationNumber()
        );

        carRequest.setProductionYear(
                car.getProductionYear()
        );

        return carRequest;
    }

    @Transactional
    public void editCar(
            UUID carId,
            CarRequest carRequest,
            String username
    ) {

        validateProductionYear(
                carRequest.getProductionYear()
        );

        Car car = findOwnedCar(
                carId,
                username
        );

        String registrationNumber =
                normalizeRegistrationNumber(
                        carRequest.getRegistrationNumber()
                );

        if (carRepository
                .existsByRegistrationNumberIgnoreCaseAndIdNot(
                        registrationNumber,
                        carId
                )) {

            throw new IllegalArgumentException(
                    "A car with this registration number already exists"
            );
        }

        car.setMake(
                carRequest.getMake().trim()
        );

        car.setModel(
                carRequest.getModel().trim()
        );

        car.setRegistrationNumber(
                registrationNumber
        );

        car.setProductionYear(
                carRequest.getProductionYear()
        );

        carRepository.save(car);

        log.info(
                "Updated car with id {} for user {}",
                carId,
                username
        );
    }

    @Transactional
    public void deleteCar(
            UUID carId,
            String username
    ) {

        Car car = findOwnedCar(
                carId,
                username
        );

        carRepository.delete(car);

        log.info(
                "Deleted car with id {} for user {}",
                carId,
                username
        );
    }

    private Car findOwnedCar(
            UUID carId,
            String username
    ) {

        return carRepository
                .findByIdAndOwnerUsername(
                        carId,
                        username
                )
                .orElseThrow(() ->
                        new CarNotFoundException(
                                "Car not found or you do not have permission to access it"
                        )
                );
    }

    private void validateProductionYear(
            Integer productionYear
    ) {

        if (productionYear == null) {

            throw new IllegalArgumentException(
                    "Production year is required"
            );
        }

        int maximumYear =
                Year.now().getValue() + 1;

        if (productionYear < 1886 ||
                productionYear > maximumYear) {

            throw new IllegalArgumentException(
                    "Production year must be between 1886 and "
                            + maximumYear
            );
        }
    }

    private String normalizeRegistrationNumber(
            String registrationNumber
    ) {

        if (registrationNumber == null ||
                registrationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Registration number is required"
            );
        }

        return registrationNumber
                .trim()
                .toUpperCase();
    }
}