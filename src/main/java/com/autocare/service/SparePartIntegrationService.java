package com.autocare.service;

import com.autocare.client.SparePartClient;
import com.autocare.dto.SparePartRequest;
import com.autocare.dto.SparePartResponse;
import com.autocare.dto.StockUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SparePartIntegrationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    SparePartIntegrationService.class
            );

    private final SparePartClient sparePartClient;

    public SparePartIntegrationService(
            SparePartClient sparePartClient
    ) {
        this.sparePartClient =
                sparePartClient;
    }

    public List<SparePartResponse> getAllParts() {

        return sparePartClient.getAllParts();
    }

    public void addPart(
            SparePartRequest request
    ) {

        SparePartResponse response =
                sparePartClient.addPart(
                        request
                );

        log.info(
                "Main application created spare part {} through parts-service",
                response.getId()
        );
    }

    public SparePartRequest getPartForEdit(
            UUID id
    ) {

        SparePartResponse sparePart =
                getAllParts()
                        .stream()
                        .filter(part ->
                                part.getId().equals(id)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Spare part not found"
                                )
                        );

        SparePartRequest request =
                new SparePartRequest();

        request.setName(
                sparePart.getName()
        );

        request.setPartNumber(
                sparePart.getPartNumber()
        );

        request.setQuantity(
                sparePart.getQuantity()
        );

        request.setPrice(
                sparePart.getPrice()
        );

        return request;
    }

    public void updatePart(
            UUID id,
            SparePartRequest request
    ) {

        sparePartClient.updatePart(
                id,
                request
        );

        log.info(
                "Main application updated spare part {} through parts-service",
                id
        );
    }

    public void updateStock(
            UUID id,
            StockUpdateRequest request
    ) {

        sparePartClient.updateStock(
                id,
                request
        );

        log.info(
                "Main application updated stock for spare part {} through parts-service",
                id
        );
    }

    public void deletePart(
            UUID id
    ) {

        sparePartClient.deletePart(
                id
        );

        log.info(
                "Main application deleted spare part {} through parts-service",
                id
        );
    }
}