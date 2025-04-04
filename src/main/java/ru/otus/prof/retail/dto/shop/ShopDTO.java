package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ShopDTO(
        @Schema(description = "Уникальный идентификатор магазина", example = "1")
        Long id,

        @NotNull(message = "Номер магазина обязателен")
        @Schema(description = "Номер магазина", example = "101")
        Long number,

        @NotBlank(message = "Название магазина обязательно")
        @Schema(description = "Название магазина", example = "Центральный")
        String name,

        @NotBlank(message = "Адрес магазина обязателен")
        @Schema(description = "Адрес магазина", example = "ул. Ленина, 1")
        String address,

        @Schema(description = "Список касс магазина")
        List<CashDTO> cashList
) {}
