package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;

public record ShopBasicDTO(
        @Schema(description = "Уникальный идентификатор магазина", example = "1")
        Long id,
        @Schema(description = "Номер магазина", example = "101")
        Long number,
        @Schema(description = "Название магазина", example = "Центральный")
        String name,
        @Schema(description = "Адрес магазина", example = "ул. Ленина, 1")
        String address
) {}