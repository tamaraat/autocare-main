package com.autocare.repository;

import com.autocare.entity.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceOfferRepository
        extends JpaRepository<ServiceOffer, UUID> {

    List<ServiceOffer> findAllByOrderByNameAsc();

    List<ServiceOffer> findAllByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            UUID id
    );
}