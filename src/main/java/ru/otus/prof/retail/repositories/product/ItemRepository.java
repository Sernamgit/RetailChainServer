package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.product.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"prices"})
    @Query("SELECT i FROM Item i WHERE i.article = :article")
    Optional<Item> findByArticleWithPrices(@Param("article") Long article);

    @EntityGraph(attributePaths = {"barcodes"})
    @Query("SELECT i FROM Item i WHERE i.article = :article")
    Optional<Item> findByArticleWithBarcodes(@Param("article") Long article);

    @Modifying
    @Query("DELETE FROM Item i WHERE i.article = :article")
    void deleteByArticle(@Param("article") Long article);
}
