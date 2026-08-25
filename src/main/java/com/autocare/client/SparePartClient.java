package com.autocare.client;

import com.autocare.dto.SparePartRequest;
import com.autocare.dto.SparePartResponse;
import com.autocare.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "parts-service",
        url = "${parts.service.url}"
)
public interface SparePartClient {

    @GetMapping("/api/parts")
    List<SparePartResponse> getAllParts();

    @PostMapping("/api/parts")
    SparePartResponse addPart(
            @RequestBody
            SparePartRequest request
    );

    @PutMapping("/api/parts/{id}")
    SparePartResponse updatePart(
            @PathVariable("id")
            UUID id,
            @RequestBody
            SparePartRequest request
    );

    @PutMapping("/api/parts/{id}/stock")
    SparePartResponse updateStock(
            @PathVariable("id")
            UUID id,
            @RequestBody
            StockUpdateRequest request
    );

    @DeleteMapping("/api/parts/{id}")
    void deletePart(
            @PathVariable("id")
            UUID id
    );
}