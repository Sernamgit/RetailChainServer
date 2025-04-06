package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.product.PriceDTO;
import ru.otus.prof.retail.exception.product.ItemNotFoundException;
import ru.otus.prof.retail.exception.product.PriceNotFoundException;
import ru.otus.prof.retail.services.product.PriceService;

import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
public class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PriceService priceService;

    private final String BASE_URL = "/api/v1/product/price";

    @Test
    void getPrice_shouldReturnPrice() throws Exception {
        PriceDTO priceDTO = new PriceDTO(1L, 19999L, 12345L);

        when(priceService.getPrice(1L)).thenReturn(priceDTO);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(19999L))
                .andExpect(jsonPath("$.article").value(12345L));
    }

    @Test
    void getPrice_WithNonExistentPriceID_ShouldReturnNotFound() throws Exception {
        when(priceService.getPrice(999L))
                .thenThrow(new PriceNotFoundException("Цена с id 999 не найдена"));

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Цена с id 999 не найдена"));
    }

    @Test
    void getPrice_WithInvalidPriceID_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPriceByArticle_ShouldReturnPrices() throws Exception {
        PriceDTO priceDTO = new PriceDTO(1L, 19999L, 12345L);

        when(priceService.getPricesByItemArticle(12345L)).thenReturn(List.of(priceDTO));

        mockMvc.perform(get(BASE_URL).param("article", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].price").value(19999L))
                .andExpect(jsonPath("$[0].article").value(12345L));
    }

    @Test
    void getPriceByArticle_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        when(priceService.getPricesByItemArticle(999L))
                .thenThrow(new ItemNotFoundException("Товар с артикулом 999 не найден"));

        mockMvc.perform(get(BASE_URL).param("article", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 999 не найден"));
    }

    @Test
    void getPriceByArticle_WithNoPrices_ShouldReturnNotFound() throws Exception {
        when(priceService.getPricesByItemArticle(12345L))
                .thenThrow(new PriceNotFoundException("Товар с артикулом 12345 не имеет цен"));

        mockMvc.perform(get(BASE_URL).param("article", "12345"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 12345 не имеет цен"));
    }

    @Test
    void getPriceByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrice_ShouldReturnCreatedPrice() throws Exception {
        PriceDTO request = new PriceDTO(null, 19999L, 12345L);
        PriceDTO response = new PriceDTO(1L, 19999L, 12345L);

        when(priceService.createPrice(any(PriceDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(19999L))
                .andExpect(jsonPath("$.article").value(12345L));
    }

    @Test
    void createPrice_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        PriceDTO request = new PriceDTO(null, 19999L, 999L);

        when(priceService.createPrice(any(PriceDTO.class)))
                .thenThrow(new ItemNotFoundException("Товар с артикулом 999 не найден"));

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 999 не найден"));
    }

    @Test
    void createPrice_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ \"price\": 0, \"article\": 0 }";

        mockMvc.perform(post(BASE_URL + "/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void createPricesBatch_ShouldReturnCreatedPrices() throws Exception {
        List<PriceDTO> request = List.of(
                new PriceDTO(null, 19999L, 12345L),
                new PriceDTO(null, 29999L, 12345L)
        );
        List<PriceDTO> response = List.of(
                new PriceDTO(1L, 19999L, 12345L),
                new PriceDTO(2L, 29999L, 12345L)
        );

        when(priceService.createPrices(anyList())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].price").value(19999L))
                .andExpect(jsonPath("$[0].article").value(12345L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].price").value(29999L))
                .andExpect(jsonPath("$[1].article").value(12345L));
    }

    @Test
    void createPricesBatch_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        List<PriceDTO> request = List.of(new PriceDTO(null, 19999L, 999L));

        when(priceService.createPrices(anyList()))
                .thenThrow(new ItemNotFoundException("Товары с артикулами [999] не найдены"));

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товары с артикулами [999] не найдены"));
    }

    @Test
    void createPricesBatch_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "[{ \"price\": 0, \"article\": 0 }]";

        mockMvc.perform(post(BASE_URL + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void deletePrice_ShouldReturnNoContent() throws Exception {
        doNothing().when(priceService).deletePrice(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(priceService, times(1)).deletePrice(1L);
    }

    @Test
    void deletePrice_WithNonExistentPriceID_ShouldReturnNotFound() throws Exception {
        doThrow(new PriceNotFoundException("Цена с id 999 не найдена"))
                .when(priceService).deletePrice(999L);

        mockMvc.perform(delete(BASE_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Цена с id 999 не найдена"));
    }

    @Test
    void deletePrice_WithInvalidPriceID_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteByArticle_ShouldReturnNoContent() throws Exception {
        doNothing().when(priceService).deleteAllPricesByItemArticle(12345L);

        mockMvc.perform(delete(BASE_URL).param("article", "12345"))
                .andExpect(status().isNoContent());

        verify(priceService, times(1)).deleteAllPricesByItemArticle(12345L);
    }

    @Test
    void deleteByArticle_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        doThrow(new ItemNotFoundException("Товар с артикулом 999 не найден"))
                .when(priceService).deleteAllPricesByItemArticle(999L);

        mockMvc.perform(delete(BASE_URL).param("article", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 999 не найден"));
    }

    @Test
    void deleteByArticle_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL).param("article", "0"))
                .andExpect(status().isBadRequest());
    }
}