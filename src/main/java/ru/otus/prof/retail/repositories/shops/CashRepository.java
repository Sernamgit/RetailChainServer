package ru.otus.prof.retail.repositories.shops;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.shops.Cash;

public interface CashRepository extends JpaRepository<Cash, Long> {
}
