package ru.otus.prof.retail.entities.product;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "barcode")
public class Barcode {
    @Id
    @Column(name = "barcode", nullable = false)
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "article", nullable = false)
    private Item item;
}
