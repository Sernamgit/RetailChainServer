package ru.otus.prof.retail.entities.purchases;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shift_number", nullable = false)
    private Long shiftNumber;

    @Column(name = "shop_number", nullable = false)
    private Long shopNumber;

    @Column(name = "cash_number", nullable = false)
    private Long cashNumber;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime openTime;

    @Column(name = "close_date")
    private LocalDateTime closeTime;

    @Column(name = "total")
    private Long total;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Purchase> purchases = new HashSet<>();

}
