package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для передачи штрих-кода товара")
public record InputBarcodeDTO(
        @Schema(description = "Значение штрих-кода", required = true, example = "123456789012")
        @NotBlank(message = "Штрих-код не может быть пустым")
        @Size(min = 8, max = 20, message = "Длина штрих-кода должна быть от 8 до 20 символов")
        @Pattern(regexp = "^[0-9]+$", message = "Штрих-код должен содержать только цифры")
        String barcode) {
}