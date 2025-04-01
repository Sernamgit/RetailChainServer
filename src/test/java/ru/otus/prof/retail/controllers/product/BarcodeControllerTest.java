package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.exception.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.BarcodeNotFoundException;
import ru.otus.prof.retail.services.product.BarcodeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BarcodeController.class)
class BarcodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BarcodeService barcodeService;

    private final String BASE_URL = "/api/v1/product/barcode";

    @Test
    void getBarcode_ShouldReturnBarcode() throws Exception {
        BarcodeDTO barcodeDTO = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.getBarcode("123456789012")).thenReturn(barcodeDTO);

        mockMvc.perform(get(BASE_URL + "/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value("123456789012"))
                .andExpect(jsonPath("$.article").value(12345L));
    }

    @Test
    void getBarcode_WithNonExistentBarcode_ShouldReturnNotFound() throws Exception {
        when(barcodeService.getBarcode("999999999999")).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/999999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBarcode_WithInvalidBarcode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBarcode_ShouldReturnNoContent() throws Exception {
        doNothing().when(barcodeService).deleteBarcode("123456789012");

        mockMvc.perform(delete(BASE_URL + "/123456789012"))
                .andExpect(status().isNoContent());

        verify(barcodeService, times(1)).deleteBarcode("123456789012");
    }

    @Test
    void deleteBarcode_WithNonExistentBarcode_ShouldReturnNotFound() throws Exception {
        doThrow(new BarcodeNotFoundException("Штрих-код не найден: 999999999999")).when(barcodeService).deleteBarcode("999999999999");

        mockMvc.perform(delete(BASE_URL + "/999999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Штрих-код не найден: 999999999999"));
    }

    @Test
    void deleteBarcode_WithInvalidBarcode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBarcodesByArticle_ShouldReturnBarcodes() throws Exception {
        BarcodeDTO barcodeDTO = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.getBarcodesByItemArticle(12345L)).thenReturn(List.of(barcodeDTO));

        mockMvc.perform(get(BASE_URL).param("article", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barcode").value("123456789012"))
                .andExpect(jsonPath("$[0].article").value(12345L));
    }

    @Test
    void getBarcodesByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBarcodes_WithSingleBarcode_ShouldReturnCreatedBarcode() throws Exception {
        BarcodeDTO request = new BarcodeDTO("123456789012", 12345L);
        BarcodeDTO response = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.createBarcode(any(BarcodeDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barcode").value("123456789012"))
                .andExpect(jsonPath("$[0].article").value(12345L));
    }

    @Test
    void createBarcodes_WithMultipleBarcodes_ShouldReturnCreatedBarcodes() throws Exception {
        List<BarcodeDTO> request = List.of(
                new BarcodeDTO("123456789012", 12345L),
                new BarcodeDTO("987654321098", 12345L)
        );
        List<BarcodeDTO> response = List.of(
                new BarcodeDTO("123456789012", 12345L),
                new BarcodeDTO("987654321098", 12345L)
        );

        when(barcodeService.createBarcodes(anyList())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barcode").value("123456789012"))
                .andExpect(jsonPath("$[0].article").value(12345L))
                .andExpect(jsonPath("$[1].barcode").value("987654321098"))
                .andExpect(jsonPath("$[1].article").value(12345L));
    }

    @Test
    void createBarcodes_WithExistingBarcode_ShouldReturnConflict() throws Exception {
        BarcodeDTO request = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.createBarcode(any(BarcodeDTO.class))).thenThrow(new BarcodeAlreadyExistsException("Штрих-код уже существует: 123456789012"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Штрих-код уже существует: 123456789012"));
    }

    @Test
    void createBarcodes_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"barcode\":\"invalid\",\"article\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBarcodes_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBarcodesByArticle_ShouldReturnNoContent() throws Exception {
        doNothing().when(barcodeService).deleteAllBarcodesByItemArticle(12345L);

        mockMvc.perform(delete(BASE_URL).param("article", "12345"))
                .andExpect(status().isNoContent());

        verify(barcodeService, times(1)).deleteAllBarcodesByItemArticle(12345L);
    }

    @Test
    void deleteBarcodesByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }
}