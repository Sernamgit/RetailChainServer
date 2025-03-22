package ru.otus.prof.retail.repositories.purchases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.purchases.Purchase;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    //все чеки возвращаются с позициями
    // Запрос всех чеков по смене
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.positions WHERE p.shift.id = :shiftId")
    List<Purchase> findPurchaseByShiftId(@Param("shiftId") Long shiftId);

    // Запрос всех чеков по магазину и дате
    @Query("SELECT p FROM Purchase p " +
            "JOIN p.shift s " +
            "WHERE s.shopNumber = :shopNumber " +
            "AND CAST(p.purchaseDate AS date) = :date")
    List<Purchase> findPurchaseByShopNumberAndDate(@Param("shopNumber") Long shopNumber, @Param("date") LocalDate date);

    // Запрос всех чеков по магазину, кассе и дате
    @Query("SELECT p FROM Purchase p " +
            "JOIN p.shift s " +
            "WHERE s.shopNumber = :shopNumber " +
            "AND s.cashNumber = :cashNumber " +
            "AND CAST(p.purchaseDate AS date) = :date")
    List<Purchase> findPurchasesByShopNumberAndCashNumberAndDate(@Param("shopNumber") Long shopNumber,
                                                                 @Param("cashNumber") Long cashNumber,
                                                                 @Param("date") LocalDate date);

}
