package ru.otus.prof.retail.controllers.shops;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.dto.shop.CashDTO;
import ru.otus.prof.retail.dto.shop.CashDeleteRequestDTO;
import ru.otus.prof.retail.dto.shop.CashStatusRequestDTO;
import ru.otus.prof.retail.exception.CashNotFoundException;
import ru.otus.prof.retail.exception.CashNumberAlreadyExistsException;
import ru.otus.prof.retail.services.shops.CashService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashController.class)
public class CashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CashService cashService;

    private final String BASE_URL = "/api/v1/cash";

    @Test
    void createCash_ShouldReturnCreatedCash() throws Exception {
        CashDTO request = new CashDTO(null, STATUS.ACTIVE, 101L, LocalDateTime.now(), LocalDateTime.now(), 1L);
        CashDTO responseDto = new CashDTO(1L, STATUS.ACTIVE, 101L, LocalDateTime.now(), LocalDateTime.now(), 1L);

        Mockito.when(cashService.createCash(any(CashDTO.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(101L))
                .andExpect(jsonPath("$.shopNumber").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createCash_WithExistingNumber_ShouldReturnConflict() throws Exception {
        CashDTO request = new CashDTO(null, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), 1L);

        Mockito.when(cashService.createCash(any(CashDTO.class))).thenThrow(new CashNumberAlreadyExistsException("Касса с номером 1 уже существует в магазине 1"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Касса с номером 1 уже существует в магазине 1"));
    }

    @Test
    void getCashById_ShouldReturnCash() throws Exception {
        CashDTO responseDto = new CashDTO(1L, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), 1L);

        Mockito.when(cashService.getCashById(1L)).thenReturn(Optional.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.number").value(1L))
                .andExpect(jsonPath("$.shopNumber").value(1L));
    }

    @Test
    void getCashById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        Mockito.when(cashService.getCashById(999L)).thenThrow(new CashNotFoundException("Касса с ID 999 не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Касса с ID 999 не найдена"));
    }

    @Test
    void getCashByNumberAndShopNumber_ShouldReturnCash() throws Exception {
        CashDTO responseDto = new CashDTO(1L, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), 1L);

        Mockito.when(cashService.getCashByNumberAndShopNumber(1L, 1L)).thenReturn(Optional.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/number/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1L))
                .andExpect(jsonPath("$.shopNumber").value(1L));
    }

    @Test
    void getCashByNumberAndShopNumber_WithNonExistingData_ShouldReturnNotFound() throws Exception {
        Mockito.when(cashService.getCashByNumberAndShopNumber(999L, 999L))
                .thenThrow(new CashNotFoundException("Касса с номером 999 в магазине 999 не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/number/999/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Касса с номером 999 в магазине 999 не найдена"));
    }

    @Test
    void getCashByShopNumber_ShouldReturnListOfCash() throws Exception {
        List<CashDTO> cashList = Collections.singletonList(
                new CashDTO(1L, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), 1L)
        );

        Mockito.when(cashService.getCashByShopNumber(1L)).thenReturn(cashList);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/shop/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shopNumber").value(1L));
    }

    @Test
    void getCashByShopNumber_WithNonExistingShop_ShouldReturnNotFound() throws Exception {
        Mockito.when(cashService.getCashByShopNumber(999L)).thenThrow(new CashNotFoundException("Кассы для магазина с номером 999 не найдены"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/shop/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Кассы для магазина с номером 999 не найдены"));
    }

    @Test
    void updateCashStatus_ById_ShouldReturnOk() throws Exception {
        CashStatusRequestDTO request = new CashStatusRequestDTO(1L, null, null, STATUS.DELETED);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCashStatus_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CashStatusRequestDTO request = new CashStatusRequestDTO(null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCashById_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(cashService).updateCashStatus(1L, STATUS.DELETED);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(cashService).updateCashStatus(1L, STATUS.DELETED);
    }

    @Test
    void deleteCashByNumberAndShop_ShouldReturnNoContent() throws Exception {
        CashDTO cashDto = new CashDTO(1L, STATUS.ACTIVE, 1L, LocalDateTime.now(), LocalDateTime.now(), 1L);

        Mockito.when(cashService.getCashByNumberAndShopNumber(1L, 1L)).thenReturn(Optional.of(cashDto));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/number/1/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(cashService).updateCashStatus(1L, STATUS.DELETED);
    }

    @Test
    void deleteCash_WithRequestDto_ShouldReturnNoContent() throws Exception {
        CashDeleteRequestDTO request = new CashDeleteRequestDTO(1L, null, null);

        Mockito.doNothing().when(cashService).updateCashStatus(1L, STATUS.DELETED);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Mockito.verify(cashService).updateCashStatus(1L, STATUS.DELETED);
    }
}