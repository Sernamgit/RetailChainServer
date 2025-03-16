package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.product.Price;

public interface PriceRepository extends JpaRepository<Price, Long > {
}
