package ru.otus.prof.retail.dto.shop;

import jakarta.validation.constraints.NotNull;
import ru.otus.prof.retail.STATUS;

public record CashStatusRequestDTO(
        Long id,
        Long number,
        Long shopNumber,
        @NotNull(message = "Статус не может быть null")
        STATUS status
) {
}
