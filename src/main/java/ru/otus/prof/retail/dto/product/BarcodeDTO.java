package ru.otus.prof.retail.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Информация о штрих-коде товара")
public record BarcodeDTO(
        @NotBlank(message = "Значение штрих-кода не может быть пустым")
        @Size(min = 8, max = 50, message = "Длина штрих-кода должна быть от 8 до 50 символов")
        @Schema(
                description = "Уникальное значение штрих-кода",
                example = "123456789012",
                minLength = 8,
                maxLength = 50
        )
        String barcode,

        @NotNull(message = "Артикул товара не может быть пустым")
        @Positive(message = "Артикул товара должен быть положительным числом")
        @Schema(
                description = "Артикул товара, к которому привязан штрих-код",
                example = "12345"
        )
        Long article
) {
}
