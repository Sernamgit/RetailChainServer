package ru.otus.prof.retail.dto.purchases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Информация о кассовой смене")
public record ShiftDTO(
        @Schema(description = "ID смены", example = "1")
        @Positive(message = "ID смены должен быть положительным числом")
        Long id,

        @Schema(description = "Номер смены", example = "123")
        @Positive(message = "Номер смены должен быть положительным числом")
        Long shiftNumber,

        @Schema(description = "Номер магазина", example = "1")
        @Positive(message = "Номер магазина должен быть положительным числом")
        Long shopNumber,

        @Schema(description = "Номер кассы", example = "1")
        @Positive(message = "Номер кассы должен быть положительным числом")
        Long cashNumber,

        @Schema(description = "Время открытия смены", example = "2023-01-01T08:00:00")
        @NotNull(message = "Время открытия смены не может быть null")
        LocalDateTime openTime,

        @Schema(description = "Время закрытия смены", example = "2023-01-01T20:00:00")
        @NotNull(message = "Время закрытия смены не может быть null")
        LocalDateTime closeTime,

        @Schema(description = "Общая сумма по смене (в копейках)", example = "150000")
        @Positive(message = "Общая сумма должна быть положительным числом")
        Long total,

        @Schema(description = "Список покупок за смену")
        Set<PurchaseDTO> purchases
) {
}