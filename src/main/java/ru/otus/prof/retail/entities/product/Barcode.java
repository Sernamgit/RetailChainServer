package ru.otus.prof.retail.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "barcode")
public class Barcode {
    @Id
    @Column(name = "barcode", nullable = false)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article", nullable = false)
    private Item item;
}
