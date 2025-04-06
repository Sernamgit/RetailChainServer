package ru.otus.prof.retail.dto.shop;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на удаление кассы")
public record CashDeleteRequestDTO(
        @Schema(description = "ID кассы для удаления", example = "5")
        Long id,

        @Schema(description = "Номер кассы для удаления", example = "3")
        Long number,

        @Schema(description = "Номер магазина, к которому привязана касса", example = "101")
        Long shopNumber
) {}