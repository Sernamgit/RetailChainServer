package ru.otus.prof.retail.dto.shop;

import ru.otus.prof.retail.STATUS;

public record CashStatusRequestDTO(
        Long id,
        Long number,
        Long shopNumber,
        STATUS status
) {
}
