package ru.otus.prof.retail.controllers.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.purchases.PurchaseDTO;
import ru.otus.prof.retail.exception.purchases.ShiftNotFoundException;
import ru.otus.prof.retail.services.purchases.PurchaseService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
public class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    private final String BASE_URL = "/api/v1/purchases/check";

    @Test
    void getPurchasesByShift_shouldReturnPurchases() throws Exception {
        PurchaseDTO purchase1 = new PurchaseDTO(1L, 10L, LocalDateTime.now(), 25000L, List.of());
        PurchaseDTO purchase2 = new PurchaseDTO(2L, 10L, LocalDateTime.now(), 35000L, List.of());

        when(purchaseService.getPurchaseByShiftId(10L)).thenReturn(List.of(purchase1, purchase2));

        mockMvc.perform(get(BASE_URL + "/shift/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].shiftId").value(10L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].shiftId").value(10L));
    }

    @Test
    void getPurchasesByShift_WithNonExistentShiftId_ShouldReturnNotFound() throws Exception {
        when(purchaseService.getPurchaseByShiftId(999L))
                .thenThrow(new ShiftNotFoundException("Смена с id 999 не найдена"));

        mockMvc.perform(get(BASE_URL + "/shift/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Смена с id 999 не найдена"));
    }

    @Test
    void getPurchasesByShift_WithNoPurchases_ShouldReturnEmptyList() throws Exception {
        when(purchaseService.getPurchaseByShiftId(10L)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/shift/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPurchasesByShift_WithInvalidShiftId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/shift/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchasesByShopAndDate_shouldReturnPurchases() throws Exception {
        String date = "2023-05-15";
        PurchaseDTO purchase = new PurchaseDTO(1L, 10L, LocalDateTime.now(), 25000L, List.of());

        when(purchaseService.getPurchaseByShoNumberAndDate(15L, LocalDate.parse(date)))
                .thenReturn(List.of(purchase));

        mockMvc.perform(get(BASE_URL + "/shop/15/date/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPurchasesByShopAndDate_WithInvalidDate_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/shop/15/date/invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Неверный формат параметра")));
    }

    @Test
    void getPurchasesByShopCashAndDate_shouldReturnPurchases() throws Exception {
        String date = "2023-05-15";
        PurchaseDTO purchase = new PurchaseDTO(1L, 10L, LocalDateTime.now(), 25000L, List.of());

        when(purchaseService.getPurchaseByShopNumberAndCashNumberAndDate(15L, 3L, LocalDate.parse(date)))
                .thenReturn(List.of(purchase));

        mockMvc.perform(get(BASE_URL + "/shop/15/cash/3/date/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPurchasesByShopCashAndDate_WithInvalidParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/shop/0/cash/0/date/invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Неверный формат параметра")));
    }

    @Test
    void getPurchasesByShiftIds_shouldReturnPurchasesMap() throws Exception {
        PurchaseDTO purchase1 = new PurchaseDTO(1L, 10L, LocalDateTime.now(), 25000L, List.of());
        PurchaseDTO purchase2 = new PurchaseDTO(2L, 11L, LocalDateTime.now(), 35000L, List.of());

        when(purchaseService.getPurchasesByShiftIds(List.of(10L, 11L)))
                .thenReturn(Map.of(
                        10L, List.of(purchase1),
                        11L, List.of(purchase2)
                ));

        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(10L, 11L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.10[0].id").value(1L))
                .andExpect(jsonPath("$.11[0].id").value(2L));
    }

    @Test
    void getPurchasesByShiftIds_WithEmptyList_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchasesByShiftIds_WithNegativeIds_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(-1L, 10L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("must be greater than 0")));
    }

    @Test
    void getPurchasesByShiftIds_WithZeroId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(0L, 10L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("must be greater than 0")));
    }

    @Test
    void getPurchasesByShiftIds_WithNull_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Неверный формат JSON")));
    }

    @Test
    void getPurchasesByShiftIds_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Неверный формат JSON")));
    }

    @Test
    void getPurchasesByShiftIds_WhenNoPurchasesFound_ShouldReturnNotFound() throws Exception {
        when(purchaseService.getPurchasesByShiftIds(anyList()))
                .thenThrow(new ShiftNotFoundException("Не найдено чеков"));

        mockMvc.perform(post(BASE_URL + "/shift/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(10L, 11L))))
                .andExpect(status().isNotFound());
    }
}