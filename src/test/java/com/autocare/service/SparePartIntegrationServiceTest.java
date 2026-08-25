package com.autocare.service;

import com.autocare.client.SparePartClient;
import com.autocare.dto.SparePartRequest;
import com.autocare.dto.SparePartResponse;
import com.autocare.dto.StockUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SparePartIntegrationServiceTest {

    @Mock
    private SparePartClient client;

    private SparePartIntegrationService service;

    @BeforeEach
    void setUp() {
        service =
                new SparePartIntegrationService(
                        client
                );
    }

    @Test
    void getAllPartsShouldDelegateToClient() {

        when(
                client.getAllParts()
        ).thenReturn(
                List.of(createResponse())
        );

        assertEquals(
                1,
                service.getAllParts().size()
        );

        verify(client).getAllParts();
    }

    @Test
    void addPartShouldCallClient() {

        SparePartRequest request =
                new SparePartRequest();

        SparePartResponse response =
                createResponse();

        when(
                client.addPart(request)
        ).thenReturn(response);

        service.addPart(request);

        verify(client).addPart(request);
    }

    @Test
    void getPartForEditShouldMapResponse() {

        SparePartResponse response =
                createResponse();

        when(
                client.getAllParts()
        ).thenReturn(
                List.of(response)
        );

        SparePartRequest request =
                service.getPartForEdit(
                        response.getId()
                );

        assertEquals(
                response.getName(),
                request.getName()
        );

        assertEquals(
                response.getPartNumber(),
                request.getPartNumber()
        );

        assertEquals(
                response.getQuantity(),
                request.getQuantity()
        );

        assertEquals(
                response.getPrice(),
                request.getPrice()
        );
    }

    @Test
    void getMissingPartForEditShouldFail() {

        when(
                client.getAllParts()
        ).thenReturn(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.getPartForEdit(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void updatePartShouldCallClient() {

        UUID id = UUID.randomUUID();

        SparePartRequest request =
                new SparePartRequest();

        service.updatePart(id, request);

        verify(client).updatePart(
                id,
                request
        );
    }

    @Test
    void updateStockShouldCallClient() {

        UUID id = UUID.randomUUID();

        StockUpdateRequest request =
                new StockUpdateRequest();

        request.setQuantity(15);

        service.updateStock(id, request);

        verify(client).updateStock(
                id,
                request
        );
    }

    @Test
    void deletePartShouldCallClient() {

        UUID id = UUID.randomUUID();

        service.deletePart(id);

        verify(client).deletePart(id);
    }

    private SparePartResponse createResponse() {

        SparePartResponse response =
                new SparePartResponse();

        response.setId(UUID.randomUUID());
        response.setName("Oil Filter");
        response.setPartNumber("OF-100");
        response.setQuantity(10);
        response.setPrice(
                new BigDecimal("25.50")
        );

        return response;
    }
}