package ru.otus.prof.retail.entities.product;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "article", nullable = false, unique = true)
    private Long article;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Price> prices;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Barcode> barcodes;
}
