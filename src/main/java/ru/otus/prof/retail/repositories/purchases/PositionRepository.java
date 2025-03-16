package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.purchases.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
