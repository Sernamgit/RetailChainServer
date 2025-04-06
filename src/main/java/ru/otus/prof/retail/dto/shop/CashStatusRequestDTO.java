package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.otus.prof.retail.STATUS;

@Schema(description = "Запрос статуса кассы")
public record CashStatusRequestDTO(
        @Schema(description = "ID кассы для изменения статуса", example = "5")
        Long id,

        @Schema(description = "Номер кассы", example = "3")
        Long number,

        @Schema(description = "Номер магазина, к которому привязана касса", example = "101")
        Long shopNumber,

        @NotNull(message = "Статус не может быть null")
        @Schema(description = "Новый статус кассы", example = "ACTIVE")
        STATUS status
) {}