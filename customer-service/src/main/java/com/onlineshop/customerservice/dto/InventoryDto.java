package com.onlineshop.customerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long inventoryId;

    private int total;

    private int available;

    private int reserved;

}
