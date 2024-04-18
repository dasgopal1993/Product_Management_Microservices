package com.onlineshop.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "total")
    private int total;

    @Column(name = "available")
    private int available;

    @Column(name = "reserved")
    private int reserved;

//    @OneToOne(mappedBy = "inventory")
//    private Product product;

}
