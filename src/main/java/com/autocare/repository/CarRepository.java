package com.autocare.repository;

import com.autocare.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findAllByOwnerUsernameOrderByMakeAscModelAsc(String username);

    Optional<Car> findByIdAndOwnerUsername(UUID id, String username);

    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);

    boolean existsByRegistrationNumberIgnoreCaseAndIdNot(
            String registrationNumber,
            UUID id
    );
}