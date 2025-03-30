package ru.otus.prof.retail.controllers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.prof.retail.dto.product.CreateItemDTO;
import ru.otus.prof.retail.dto.product.ItemDTO;
import ru.otus.prof.retail.dto.product.UpdateItemDTO;
import ru.otus.prof.retail.exception.ItemNotFoundException;
import ru.otus.prof.retail.exception.ItemValidationException;
import ru.otus.prof.retail.services.product.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final String BASE_URL = "/api/v1/product/item";

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        CreateItemDTO request = new CreateItemDTO(12345L, "Test Item", Collections.emptySet(), Collections.emptySet());

        ItemDTO response = new ItemDTO(
                12345L, "Test Item",
                LocalDateTime.now(), LocalDateTime.now(),
                Collections.emptySet(), Collections.emptySet()
        );

        when(itemService.createItem(any(CreateItemDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article").value(12345L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void createItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"article\":null,\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_WithExistingArticle_ShouldReturnBadRequest() throws Exception {
        CreateItemDTO request = new CreateItemDTO(12345L, "Test Item", Collections.emptySet(), Collections.emptySet());

        when(itemService.createItem(any(CreateItemDTO.class))).thenThrow(new ItemValidationException("Товар с артикулом 12345 уже существует"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 12345 уже существует"));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        ItemDTO response = new ItemDTO(
                12345L, "Test Item",
                LocalDateTime.now(), LocalDateTime.now(),
                Collections.emptySet(), Collections.emptySet()
        );

        when(itemService.getItem(12345L)).thenReturn(Optional.of(response));

        mockMvc.perform(get(BASE_URL + "/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article").value(12345L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getItem_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        when(itemService.getItem(99999L)).thenThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден"));

        mockMvc.perform(get(BASE_URL + "/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void getItem_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/0")).andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        UpdateItemDTO request = new UpdateItemDTO(12345L, "Updated Item", Collections.emptySet(), Collections.emptySet());

        ItemDTO response = new ItemDTO(
                12345L, "Updated Item",
                LocalDateTime.now(), LocalDateTime.now(),
                Collections.emptySet(), Collections.emptySet()
        );

        when(itemService.updateItem(any(UpdateItemDTO.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article").value(12345L))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void updateItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("{\"article\":null,\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        UpdateItemDTO request = new UpdateItemDTO(99999L, "Updated Item", Collections.emptySet(), Collections.emptySet());

        when(itemService.updateItem(any(UpdateItemDTO.class))).thenThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден"));

        mockMvc.perform(put(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void deleteItem_ShouldReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItem(12345L);

        mockMvc.perform(delete(BASE_URL + "/12345")).andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItem(12345L);
    }

    @Test
    void deleteItem_WithNonExistentArticle_ShouldReturnNotFound() throws Exception {
        doThrow(new ItemNotFoundException("Товар с артикулом 99999 не найден")).when(itemService).deleteItem(99999L);

        mockMvc.perform(delete(BASE_URL + "/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар с артикулом 99999 не найден"));
    }

    @Test
    void deleteItem_WithInvalidArticle_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/0")).andExpect(status().isBadRequest());
    }
}