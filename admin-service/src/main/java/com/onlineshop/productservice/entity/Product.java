package com.onlineshop.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long productId;
    private String name;
    private String brand;
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_price_id")
    private Price price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_inventory_id")
    private Inventory inventory;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_product_id", referencedColumnName = "product_id")
    List<Attribute> attributes;

}
