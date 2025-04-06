package ru.otus.prof.retail.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.prof.retail.exception.shop.ShopNotFoundException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerIntegrationTest.TestController.class)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestController testController;

    @Test
    void whenShopNotFound_Returns404() throws Exception {
        when(testController.getShop()).thenThrow(new ShopNotFoundException("Магазин не найден"));

        mockMvc.perform(get("/test/shop"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Магазин не найден"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/shop")
        public String getShop() {
            return "OK";
        }
    }
}