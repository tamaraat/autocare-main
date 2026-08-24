package com.autocare.service;

import com.autocare.dto.ServiceOfferRequest;
import com.autocare.entity.ServiceOffer;
import com.autocare.exception.ServiceOfferNotFoundException;
import com.autocare.repository.ServiceOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceOfferService {

    private static final Logger log =
            LoggerFactory.getLogger(ServiceOfferService.class);

    private final ServiceOfferRepository serviceOfferRepository;

    public ServiceOfferService(
            ServiceOfferRepository serviceOfferRepository
    ) {
        this.serviceOfferRepository =
                serviceOfferRepository;
    }

    public List<ServiceOffer> getActiveServices() {

        return serviceOfferRepository
                .findAllByActiveTrueOrderByNameAsc();
    }

    public List<ServiceOffer> getAllServices() {

        return serviceOfferRepository
                .findAllByOrderByNameAsc();
    }

    @Transactional
    public void addService(
            ServiceOfferRequest request
    ) {

        String name = request
                .getName()
                .trim();

        if (serviceOfferRepository
                .existsByNameIgnoreCase(name)) {

            throw new IllegalArgumentException(
                    "A service with this name already exists"
            );
        }

        ServiceOffer serviceOffer =
                new ServiceOffer();

        serviceOffer.setName(name);

        serviceOffer.setPrice(
                request.getPrice()
        );

        serviceOffer.setDurationMinutes(
                request.getDurationMinutes()
        );

        serviceOffer.setActive(true);

        serviceOfferRepository.save(
                serviceOffer
        );

        log.info(
                "Created service offer with id {}",
                serviceOffer.getId()
        );
    }

    public ServiceOfferRequest getServiceForEdit(
            UUID serviceId
    ) {

        ServiceOffer serviceOffer =
                findService(serviceId);

        ServiceOfferRequest request =
                new ServiceOfferRequest();

        request.setName(
                serviceOffer.getName()
        );

        request.setPrice(
                serviceOffer.getPrice()
        );

        request.setDurationMinutes(
                serviceOffer.getDurationMinutes()
        );

        return request;
    }

    @Transactional
    public void editService(
            UUID serviceId,
            ServiceOfferRequest request
    ) {

        ServiceOffer serviceOffer =
                findService(serviceId);

        String name = request
                .getName()
                .trim();

        if (serviceOfferRepository
                .existsByNameIgnoreCaseAndIdNot(
                        name,
                        serviceId
                )) {

            throw new IllegalArgumentException(
                    "A service with this name already exists"
            );
        }

        serviceOffer.setName(name);

        serviceOffer.setPrice(
                request.getPrice()
        );

        serviceOffer.setDurationMinutes(
                request.getDurationMinutes()
        );

        serviceOfferRepository.save(
                serviceOffer
        );

        log.info(
                "Updated service offer with id {}",
                serviceId
        );
    }

    @Transactional
    public void toggleService(
            UUID serviceId
    ) {

        ServiceOffer serviceOffer =
                findService(serviceId);

        serviceOffer.setActive(
                !serviceOffer.isActive()
        );

        serviceOfferRepository.save(
                serviceOffer
        );

        log.info(
                "Changed service {} active status to {}",
                serviceId,
                serviceOffer.isActive()
        );
    }

    private ServiceOffer findService(
            UUID serviceId
    ) {

        return serviceOfferRepository
                .findById(serviceId)
                .orElseThrow(() ->
                        new ServiceOfferNotFoundException(
                                "Service offer not found"
                        )
                );
    }
}