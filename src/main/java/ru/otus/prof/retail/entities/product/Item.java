package ru.otus.prof.retail.entities.product;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

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

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Price> prices;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Barcode> barcodes;

    @PrePersist
    @PreUpdate
    public void setTimestamps() {
        if (createDate == null) {
            createDate = LocalDateTime.now();
        }
        updateDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(article, item.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(article);
    }

}
