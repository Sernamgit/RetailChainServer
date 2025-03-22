package ru.otus.prof.retail.repositories.shops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.shops.Shop;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    //поиск по номеру магазина
    Optional<Shop> findByNumber(Long number);

    //поиск магазина по номеру с кассами
    @Query("SELECT s FROM Shop s LEFT JOIN FETCH s.cashList WHERE s.number = :number")
    Optional<Shop> findByNumberWitchCash(@Param("number") Long number);

    //TODO лучше по номеру магазина?
    @Modifying
    @Query("DELETE FROM Shop s WHERE s.id = :id")
    void deleteShopById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Cash c WHERE c.shop.number = :shopNumber")
    void deleteCashByShopNumber(@Param("shopNumber") Long shopNumber);

}
