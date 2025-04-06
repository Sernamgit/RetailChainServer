package ru.otus.prof.retail.controllers.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.purchases.PositionDTO;
import ru.otus.prof.retail.exception.purchases.PurchaseNotFoundException;
import ru.otus.prof.retail.services.purchases.PositionService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
public class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PositionService positionService;

    private final String BASE_URL = "/api/v1/purchases/position";

    @Test
    void getPositionsByPurchase_shouldReturnPositions() throws Exception {
        PositionDTO position1 = new PositionDTO(1L, 100L, "123456789012", 12345L, "Товар 1", 10000L);
        PositionDTO position2 = new PositionDTO(2L, 100L, "123456789013", 12346L, "Товар 2", 15000L);

        when(positionService.getPositionsByPurchaseId(100L)).thenReturn(List.of(position1, position2));

        mockMvc.perform(get(BASE_URL + "/purchase/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].purchaseId").value(100L))
                .andExpect(jsonPath("$[0].barcode").value("123456789012"))
                .andExpect(jsonPath("$[0].article").value(12345L))
                .andExpect(jsonPath("$[0].positionName").value("Товар 1"))
                .andExpect(jsonPath("$[0].price").value(10000L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].purchaseId").value(100L))
                .andExpect(jsonPath("$[1].barcode").value("123456789013"))
                .andExpect(jsonPath("$[1].article").value(12346L))
                .andExpect(jsonPath("$[1].positionName").value("Товар 2"))
                .andExpect(jsonPath("$[1].price").value(15000L));
    }

    @Test
    void getPositionsByPurchase_WithNonExistentPurchaseId_ShouldReturnNotFound() throws Exception {
        when(positionService.getPositionsByPurchaseId(999L))
                .thenThrow(new PurchaseNotFoundException("Чек с id 999 не найден"));

        mockMvc.perform(get(BASE_URL + "/purchase/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Чек с id 999 не найден"));
    }

    @Test
    void getPositionsByPurchase_WithNoPositions_ShouldReturnEmptyList() throws Exception {
        when(positionService.getPositionsByPurchaseId(100L)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/purchase/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPositionsByPurchase_WithInvalidPurchaseId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/purchase/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPositionsByPurchaseIds_shouldReturnPositionsMap() throws Exception {
        PositionDTO position1 = new PositionDTO(1L, 100L, "123456789012", 12345L, "Товар 1", 10000L);
        PositionDTO position2 = new PositionDTO(2L, 101L, "123456789013", 12346L, "Товар 2", 15000L);

        when(positionService.getPositionsByPurchaseIds(List.of(100L, 101L)))
                .thenReturn(Map.of(
                        100L, List.of(position1),
                        101L, List.of(position2)
                ));

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(100L, 101L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.100[0].id").value(1L))
                .andExpect(jsonPath("$.101[0].id").value(2L));
    }

    @Test
    void getPositionsByPurchaseIds_WithEmptyList_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPositionsByPurchaseIds_WithInvalidIds_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(-1L, 0L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("must be greater than 0")));
    }

    @Test
    void getPositionsByPurchaseIds_WhenNoPositionsFound_ShouldReturnNotFound() throws Exception {
        when(positionService.getPositionsByPurchaseIds(anyList()))
                .thenThrow(new PurchaseNotFoundException("Не найдено позиций"));

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(100L, 101L))))
                .andExpect(status().isNotFound());
    }

}