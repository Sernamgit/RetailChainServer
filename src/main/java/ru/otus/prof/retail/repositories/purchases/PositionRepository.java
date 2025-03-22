package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.purchases.Position;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {


    List<Position> findByPurchase_Id(Long purchaseId);
}
