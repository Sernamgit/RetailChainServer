package ru.otus.prof.retail.repositories.product;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.prof.retail.entities.product.Item;

public interface ItemRepository extends JpaRepository<Item, Long > {

}
