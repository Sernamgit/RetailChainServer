package ru.otus.prof.retail.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;

@Schema(description = "DTO кассы")
public record CashDTO(
        @Schema(description = "Уникальный идентификатор кассы", example = "1")
        Long id,

        @NotNull(message = "Статус кассы не может быть null")
        @Schema(description = "Текущий статус кассы", example = "ACTIVE")
        STATUS status,

        @NotNull(message = "Номер кассы не может быть null")
        @Schema(description = "Уникальный номер кассы в магазине", example = "1")
        Long number,

        @Schema(description = "Дата и время создания кассы", example = "2023-05-15T10:30:00")
        LocalDateTime createDate,

        @Schema(description = "Дата и время последнего обновления", example = "2023-05-15T14:45:00")
        LocalDateTime updateDate,

        @NotNull(message = "Номер магазина не может быть null")
        @Schema(description = "Номер магазина, к которому привязана касса", example = "101")
        Long shopNumber
) {}
