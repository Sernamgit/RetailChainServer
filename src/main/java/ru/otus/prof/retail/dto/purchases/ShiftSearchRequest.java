package ru.otus.prof.retail.dto.purchases;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Параметры поиска смен")
public record ShiftSearchRequest(
        @Schema(description = "Номер магазина", example = "1")
        @Positive(message = "Номер магазина должен быть положительным числом")
        Long shopNumber,

        @Schema(description = "Номер кассы", example = "1")
        @Positive(message = "Номер кассы должен быть положительным числом")
        Long cashNumber,

        @Schema(description = "Дата закрытия смены (используется при поиске по одной дате)",
                example = "2023-01-01")
        LocalDate date,

        @Schema(description = "Начальная дата диапазона (используется при поиске по диапазону)",
                example = "2023-01-01")
        LocalDate startDate,

        @Schema(description = "Конечная дата диапазона (используется при поиске по диапазону)",
                example = "2023-01-31")
        LocalDate endDate,

        @Schema(description = "Включить информацию о покупках", example = "false")
        boolean withPurchases
) {
    @AssertTrue(message = "Должна быть указана либо одна дата (date), либо диапазон дат (startDate и endDate)")
    public boolean isDateOrRangeValid() {
        return (date != null && startDate == null && endDate == null) ||
                (date == null && startDate != null && endDate != null);
    }

    public boolean isDateRangeSearch() {
        return startDate != null && endDate != null;
    }
}