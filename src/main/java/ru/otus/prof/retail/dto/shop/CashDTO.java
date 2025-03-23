package ru.otus.prof.retail.dto.shop;

import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;

public record CashDTO(
        Long id,
        STATUS status,
        Long number,
        LocalDateTime createDate,
        LocalDateTime updateDate,
        Long shopNumber
) {
}
