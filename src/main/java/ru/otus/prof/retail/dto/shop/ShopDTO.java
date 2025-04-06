package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DTO магазина")
public record ShopDTO(
        @Schema(description = "Уникальный идентификатор магазина", example = "1")
        Long id,

        @NotNull(message = "Номер магазина обязателен")
        @Schema(description = "Уникальный номер магазина в системе", example = "101")
        Long number,

        @NotBlank(message = "Название магазина обязательно")
        @Schema(description = "Наименование магазина", example = "Центральный")
        String name,

        @NotBlank(message = "Адрес магазина обязателен")
        @Schema(description = "Физический адрес магазина", example = "ул. Ленина, 1")
        String address,

        @Schema(description = "Список касс, принадлежащих магазину")
        List<CashDTO> cashList
) {}
