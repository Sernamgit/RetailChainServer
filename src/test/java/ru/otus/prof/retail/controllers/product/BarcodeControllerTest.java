package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.product.BarcodeDTO;
import ru.otus.prof.retail.exception.product.BarcodeAlreadyExistsException;
import ru.otus.prof.retail.exception.product.BarcodeNotFoundException;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.services.product.BarcodeService;

import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
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
        when(barcodeService.getBarcode("999999999999"))
                .thenThrow(new BarcodeNotFoundException("Штрих-код 999999999999 не найден"));

        mockMvc.perform(get(BASE_URL + "/999999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Штрих-код 999999999999 не найден"));
    }

    @Test
    void getBarcode_WithInvalidBarcode_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/short"))
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
        doThrow(new BarcodeNotFoundException("Штрих-код не найден: 999999999999"))
                .when(barcodeService).deleteBarcode("999999999999");

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
    void getBarcodesByArticle_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        when(barcodeService.getBarcodesByItemArticle(99999L))
                .thenThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден"));

        mockMvc.perform(get(BASE_URL).param("article", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void getBarcodesByArticle_WithNoBarcodes_ShouldReturnNotFound() throws Exception {
        when(barcodeService.getBarcodesByItemArticle(12345L))
                .thenThrow(new BarcodeNotFoundException("Товар с артикулом 12345 не имеет штрих-кодов"));

        mockMvc.perform(get(BASE_URL).param("article", "12345"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 12345 не имеет штрих-кодов"));
    }

    @Test
    void getBarcodesByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBarcode_ShouldReturnCreatedBarcode() throws Exception {
        BarcodeDTO request = new BarcodeDTO("123456789012", 12345L);
        BarcodeDTO response = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.createBarcode(any(BarcodeDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value("123456789012"))
                .andExpect(jsonPath("$.article").value(12345L));
    }

    @Test
    void createBarcode_WithExistingBarcode_ShouldReturnConflict() throws Exception {
        BarcodeDTO request = new BarcodeDTO("123456789012", 12345L);

        when(barcodeService.createBarcode(any(BarcodeDTO.class)))
                .thenThrow(new BarcodeAlreadyExistsException("Штрих-код уже существует: 123456789012"));

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Штрих-код уже существует: 123456789012"));
    }

    @Test
    void createBarcode_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        BarcodeDTO request = new BarcodeDTO("123456789012", 99999L);

        when(barcodeService.createBarcode(any(BarcodeDTO.class)))
                .thenThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден"));

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void createBarcode_WithInvalidBarcode_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ \"barcode\": \"short\", \"article\": 0 }";

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void createBarcodesBatch_ShouldReturnCreatedBarcodes() throws Exception {
        List<BarcodeDTO> request = List.of(
                new BarcodeDTO("123456789012", 12345L),
                new BarcodeDTO("987654321098", 12345L)
        );
        List<BarcodeDTO> response = List.of(
                new BarcodeDTO("123456789012", 12345L),
                new BarcodeDTO("987654321098", 12345L)
        );

        when(barcodeService.createBarcodes(anyList())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].barcode").value("123456789012"))
                .andExpect(jsonPath("$[0].article").value(12345L))
                .andExpect(jsonPath("$[1].barcode").value("987654321098"))
                .andExpect(jsonPath("$[1].article").value(12345L));
    }

    @Test
    void createBarcodesBatch_WithExistingBarcode_ShouldReturnConflict() throws Exception {
        List<BarcodeDTO> request = List.of(new BarcodeDTO("123456789012", 12345L));

        when(barcodeService.createBarcodes(anyList()))
                .thenThrow(new BarcodeAlreadyExistsException("Некоторые штрих-коды уже существуют"));

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Некоторые штрих-коды уже существуют"));
    }

    @Test
    void createBarcodesBatch_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        List<BarcodeDTO> request = List.of(new BarcodeDTO("123456789012", 99999L));

        when(barcodeService.createBarcodes(anyList()))
                .thenThrow(new ItemNotFoundException("Товары с артикулами [99999] не найдены"));

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товары с артикулами [99999] не найдены"));
    }

    @Test
    void createBarcodesBatch_WithInvalidBarcodeList_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "[{ \"barcode\": \"short\", \"article\": 0 }]";

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void createBarcodesBatch_WithEmptyList_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("must not be empty")));
    }

    @Test
    void deleteBarcodesByArticle_ShouldReturnNoContent() throws Exception {
        doNothing().when(barcodeService).deleteAllBarcodesByItemArticle(12345L);

        mockMvc.perform(delete(BASE_URL).param("article", "12345"))
                .andExpect(status().isNoContent());

        verify(barcodeService, times(1)).deleteAllBarcodesByItemArticle(12345L);
    }

    @Test
    void deleteBarcodesByArticle_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        doThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден"))
                .when(barcodeService).deleteAllBarcodesByItemArticle(99999L);

        mockMvc.perform(delete(BASE_URL).param("article", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void deleteBarcodesByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }
}