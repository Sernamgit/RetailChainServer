package ru.otus.prof.retail.dto.shop;

public record CashDeleteRequestDTO(
        Long id,
        Long number,
        Long shopNumber
) {
}