package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Упрощенный DTO магазина без списка касс")
public record ShopBasicDTO(
        @Schema(description = "Уникальный идентификатор магазина", example = "1")
        Long id,

        @Schema(description = "Номер магазина в системе", example = "101")
        Long number,

        @Schema(description = "Короткое название магазина", example = "Центральный")
        String name,

        @Schema(description = "Адрес расположения магазина", example = "ул. Ленина, 1")
        String address
) {}