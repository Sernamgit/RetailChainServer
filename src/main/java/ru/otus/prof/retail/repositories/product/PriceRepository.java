package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.product.Price;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByItem_Article(Long article);

    @Modifying
    @Query("DELETE FROM Price p WHERE p.item.article = :article")
    void deleteAllByItemArticle(@Param("article") Long article);
}
