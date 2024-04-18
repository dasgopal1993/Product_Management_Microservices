package com.onlineshop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    private String name;
    private String brand;
    private String description;
    private PriceDto price;
    private InventoryDto inventory;
    List<AttributeDto> attributes;
}
