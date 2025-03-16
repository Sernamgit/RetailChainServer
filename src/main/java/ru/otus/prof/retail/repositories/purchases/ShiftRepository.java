package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.purchases.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
