package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.purchases.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
