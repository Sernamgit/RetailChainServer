package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.product.Barcode;

public interface BarcodeRepository extends JpaRepository<Barcode, String > {
}
