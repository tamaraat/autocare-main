package com.autocare.service;

import com.autocare.dto.ServiceOfferRequest;
import com.autocare.entity.ServiceOffer;
import com.autocare.exception.ServiceOfferNotFoundException;
import com.autocare.repository.ServiceOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOfferServiceTest {

    @Mock
    private ServiceOfferRepository repository;

    private ServiceOfferService service;

    @BeforeEach
    void setUp() {
        service =
                new ServiceOfferService(
                        repository
                );
    }

    @Test
    void getActiveServicesShouldReturnRepositoryResult() {

        when(
                repository
                        .findAllByActiveTrueOrderByNameAsc()
        ).thenReturn(
                List.of(new ServiceOffer())
        );

        assertEquals(
                1,
                service.getActiveServices().size()
        );
    }

    @Test
    void getAllServicesShouldReturnRepositoryResult() {

        when(
                repository
                        .findAllByOrderByNameAsc()
        ).thenReturn(
                List.of(new ServiceOffer())
        );

        assertEquals(
                1,
                service.getAllServices().size()
        );
    }

    @Test
    void addServiceShouldSaveNewService() {

        ServiceOfferRequest request =
                createRequest(
                        " Oil Change ",
                        new BigDecimal("80"),
                        60
                );

        when(
                repository
                        .existsByNameIgnoreCase(
                                "Oil Change"
                        )
        ).thenReturn(false);

        service.addService(request);

        ArgumentCaptor<ServiceOffer> captor =
                ArgumentCaptor.forClass(
                        ServiceOffer.class
                );

        verify(
                repository
        ).save(captor.capture());

        ServiceOffer saved =
                captor.getValue();

        assertEquals(
                "Oil Change",
                saved.getName()
        );

        assertEquals(
                new BigDecimal("80"),
                saved.getPrice()
        );

        assertEquals(
                60,
                saved.getDurationMinutes()
        );

        assertTrue(saved.isActive());
    }

    @Test
    void addDuplicateServiceShouldFail() {

        ServiceOfferRequest request =
                createRequest(
                        "Oil Change",
                        new BigDecimal("80"),
                        60
                );

        when(
                repository
                        .existsByNameIgnoreCase(
                                "Oil Change"
                        )
        ).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addService(request)
        );
    }

    @Test
    void getServiceForEditShouldMapEntity() {

        UUID id = UUID.randomUUID();

        ServiceOffer offer =
                createOffer();

        when(
                repository.findById(id)
        ).thenReturn(
                Optional.of(offer)
        );

        ServiceOfferRequest result =
                service.getServiceForEdit(id);

        assertEquals(
                offer.getName(),
                result.getName()
        );

        assertEquals(
                offer.getPrice(),
                result.getPrice()
        );

        assertEquals(
                offer.getDurationMinutes(),
                result.getDurationMinutes()
        );
    }

    @Test
    void editServiceShouldUpdateEntity() {

        UUID id = UUID.randomUUID();

        ServiceOffer offer =
                createOffer();

        ServiceOfferRequest request =
                createRequest(
                        "Brake Service",
                        new BigDecimal("150"),
                        90
                );

        when(
                repository.findById(id)
        ).thenReturn(
                Optional.of(offer)
        );

        when(
                repository
                        .existsByNameIgnoreCaseAndIdNot(
                                "Brake Service",
                                id
                        )
        ).thenReturn(false);

        service.editService(
                id,
                request
        );

        assertEquals(
                "Brake Service",
                offer.getName()
        );

        assertEquals(
                new BigDecimal("150"),
                offer.getPrice()
        );

        assertEquals(
                90,
                offer.getDurationMinutes()
        );

        verify(repository).save(offer);
    }

    @Test
    void toggleServiceShouldChangeStatus() {

        UUID id = UUID.randomUUID();

        ServiceOffer offer =
                createOffer();

        offer.setActive(true);

        when(
                repository.findById(id)
        ).thenReturn(
                Optional.of(offer)
        );

        service.toggleService(id);

        assertFalse(offer.isActive());

        verify(repository).save(offer);
    }

    @Test
    void missingServiceShouldThrowException() {

        UUID id = UUID.randomUUID();

        when(
                repository.findById(id)
        ).thenReturn(Optional.empty());

        assertThrows(
                ServiceOfferNotFoundException.class,
                () ->
                        service.getServiceForEdit(id)
        );
    }

    private ServiceOfferRequest createRequest(
            String name,
            BigDecimal price,
            Integer duration
    ) {

        ServiceOfferRequest request =
                new ServiceOfferRequest();

        request.setName(name);
        request.setPrice(price);
        request.setDurationMinutes(duration);

        return request;
    }

    private ServiceOffer createOffer() {

        ServiceOffer offer =
                new ServiceOffer();

        offer.setId(UUID.randomUUID());
        offer.setName("Oil Change");
        offer.setPrice(
                new BigDecimal("80")
        );
        offer.setDurationMinutes(60);
        offer.setActive(true);

        return offer;
    }
}