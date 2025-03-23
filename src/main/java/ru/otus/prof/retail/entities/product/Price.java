package ru.otus.prof.retail.entities.product;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "article", nullable = false)
    private Item item;
}
