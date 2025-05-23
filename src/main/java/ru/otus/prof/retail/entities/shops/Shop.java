package ru.otus.prof.retail.entities.shops;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private Long number;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cash> cashList;

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", cashList=" + (cashList != null ? cashList.size() + " items" : "null") +
                '}';
    }

}
