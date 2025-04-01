package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.prof.retail.entities.product.Barcode;

import java.util.List;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, String> {

    List<Barcode> findByItemArticle(Long article);

    @Modifying
    @Query("DELETE FROM Barcode b WHERE b.item.article = :article")
    void deleteAllByItemArticle(@Param("article") Long article);

    boolean existsByBarcodeIn(List<String> barcodes);
}