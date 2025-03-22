package ru.otus.prof.retail.repositories.shops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.STATUS;
import ru.otus.prof.retail.entities.shops.Cash;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {

    //поиск по номеру и магазину
    Optional<Cash> findByNumberAndShopNumber(Long number, Long shopNumber);

    List<Cash> findByShopNumber(Long shopNumber);

    //Обновление статуса. Используем вместо delete. TODO стоит ли под delete и подсунуть смену статуса?
    @Modifying
    @Query("UPDATE Cash c SET c.status = :status WHERE c.id = :id")
    void updateCashStatus(@Param("id") Long id, @Param("status") STATUS status);

}
