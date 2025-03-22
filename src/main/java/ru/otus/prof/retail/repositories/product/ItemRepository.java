package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.product.Item;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Query("DELETE FROM Item i WHERE i.article = :article")
    void deleteByArticle(@Param("article") Long article);
}
