package ru.otus.prof.retail.dto.shop;

import java.util.List;

public record ShopDTO(
        Long id,
        Long number,
        String name,
        String address,
        List<CashDTO> cashList
) {
}
