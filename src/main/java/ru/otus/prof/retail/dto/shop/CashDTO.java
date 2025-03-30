package ru.otus.prof.retail.dto.shop;

import jakarta.validation.constraints.NotNull;
import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;

public record CashDTO(
        Long id,
        @NotNull(message = "Статус кассы не может быть null")
        STATUS status,
        @NotNull(message = "Номер кассы не может быть null")
        Long number,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        @NotNull(message = "Номер магазина не может быть null")
        Long shopNumber
) {
}
