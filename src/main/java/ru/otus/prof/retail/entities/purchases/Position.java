package ru.otus.prof.retail.entities.purchases;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase", nullable = false)
    private Purchase purchase;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "article", nullable = false)
    private Long article;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    @Column(name = "price", nullable = false)
    private Long price;
}
