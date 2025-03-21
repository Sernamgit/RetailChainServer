package ru.otus.prof.retail.entities.shops;

import jakarta.persistence.*;

import lombok.Data;
import ru.otus.prof.retail.STATUS;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "cash")
public class Cash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private STATUS status;

    @Column(name = "number", nullable = false)
    private Long number;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "shop_number", nullable = false)
    private Shop shop;
}
