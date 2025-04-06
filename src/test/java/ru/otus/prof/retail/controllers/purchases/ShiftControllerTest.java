package ru.otus.prof.retail.controllers.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.purchases.ShiftDTO;
import ru.otus.prof.retail.dto.purchases.ShiftSearchRequest;
import ru.otus.prof.retail.exception.purchases.ShiftNotFoundException;
import ru.otus.prof.retail.exception.purchases.ShiftValidationException;
import ru.otus.prof.retail.services.purchases.ShiftService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftController.class)
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShiftService shiftService;

    private final String BASE_URL = "/api/v1/purchases/shift";

    @Test
    void getAllShiftsByCloseDate_ShouldReturnShifts() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        when(shiftService.getAllShiftsByCloseDate(any(LocalDate.class), anyBoolean()))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(get(BASE_URL + "/date/{date}", "2024-01-01")
                        .param("withPurchases", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shiftNumber").value(1L))
                .andExpect(jsonPath("$[0].shopNumber").value(1L))
                .andExpect(jsonPath("$[0].total").value(201L));
    }

    @Test
    void getAllShiftsByCloseDate_NotFound_ShouldReturn404() throws Exception {
        when(shiftService.getAllShiftsByCloseDate(any(LocalDate.class), anyBoolean()))
                .thenThrow(new ShiftNotFoundException("Смены не найдены за 2024-01-01"));

        mockMvc.perform(get(BASE_URL + "/date/{date}", "2024-01-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Смены не найдены за 2024-01-01"));
    }

    @Test
    void getAllShiftsByCloseDateRange_ShouldReturnShifts() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        when(shiftService.getAllShiftsByCloseDateRange(any(LocalDate.class), any(LocalDate.class), anyBoolean()))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(get(BASE_URL + "/range")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("withPurchases", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shiftNumber").value(1L));
    }

    @Test
    void getAllShiftsByCloseDateRange_InvalidRange_ShouldReturn400() throws Exception {
        when(shiftService.getAllShiftsByCloseDateRange(any(LocalDate.class), any(LocalDate.class), anyBoolean()))
                .thenThrow(new ShiftValidationException("Начальная дата позже конечной"));

        mockMvc.perform(get(BASE_URL + "/range")
                        .param("startDate", "2024-01-31")
                        .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Начальная дата позже конечной"));
    }

    @Test
    void getShiftsByShopNumberAndCloseDate_ShouldReturnShifts() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        when(shiftService.getShiftsByShopNumberAndCloseDate(anyLong(), any(LocalDate.class), anyBoolean()))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(get(BASE_URL + "/shop/{shopNumber}/date/{date}", 1L, "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shopNumber").value(1L));
    }

    @Test
    void getShiftsByShopNumberAndCashNumberAndCloseDate_ShouldReturnShift() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        when(shiftService.getShiftsByShopNumberAndCashNumberAndCloseDate(anyLong(), anyLong(), any(LocalDate.class), anyBoolean()))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(get(BASE_URL + "/shop/{shopNumber}/cash/{cashNumber}/date/{date}", 1L, 1L, "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cashNumber").value(1L));
    }

    @Test
    void searchShifts_ShouldReturnShifts() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        ShiftSearchRequest request = new ShiftSearchRequest(
                1L, null, LocalDate.of(2024, 1, 1), null, null, false);

        when(shiftService.searchShifts(any(ShiftSearchRequest.class)))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(post(BASE_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shopNumber").value(1L));
    }

    @Test
    void searchShifts_InvalidRequest_ShouldReturn400() throws Exception {
        String invalidJson = "{ \"shopNumber\": 1, \"date\": \"2024-01-01\", " +
                "\"startDate\": \"2024-01-01\", \"endDate\": \"2024-01-02\", " +
                "\"withPurchases\": false }";

        mockMvc.perform(post(BASE_URL + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void searchShiftsBatch_ShouldReturnShifts() throws Exception {
        ShiftDTO shiftDTO = new ShiftDTO(
                1L, 1L, 1L, 1L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusHours(12),
                201L, null
        );

        List<ShiftSearchRequest> requests = List.of(
                new ShiftSearchRequest(1L, null, LocalDate.of(2024, 1, 1), null, null, false)
        );

        when(shiftService.searchShiftsBatch(anyList()))
                .thenReturn(List.of(shiftDTO));

        mockMvc.perform(post(BASE_URL + "/search/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shopNumber").value(1L));
    }

    @Test
    void searchShiftsBatch_EmptyRequest_ShouldReturn400() throws Exception {
        mockMvc.perform(post(BASE_URL + "/search/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("must not be empty")));
    }
}