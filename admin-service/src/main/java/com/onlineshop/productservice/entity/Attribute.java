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
@Table(name = "product_attribute")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attribute_id")
    private Long attributeId;

    private String name;
    private String value;
}
