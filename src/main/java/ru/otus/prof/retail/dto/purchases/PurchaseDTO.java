package ru.otus.prof.retail.dto.purchases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO чека покупки")
public record PurchaseDTO(
        @Schema(description = "ID чека", example = "1")
        Long id,

        @Schema(description = "ID смены", example = "5")
        @NotNull(message = "ID смены не может быть null")
        Long shiftId,

        @Schema(description = "Дата и время покупки", example = "2023-05-15T14:30:00")
        @NotNull(message = "Дата покупки не может быть null")
        LocalDateTime purchaseDate,

        @Schema(description = "Общая сумма чека в копейках", example = "12000")
        @NotNull(message = "Общая сумма не может быть null")
        @PositiveOrZero(message = "Общая сумма должна быть положительной или нулем")
        Long total,

        @Schema(description = "Список позиций в чеке")
        List<PositionDTO> positions
) {
}