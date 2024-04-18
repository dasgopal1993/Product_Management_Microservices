package com.onlineshop.productservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    @Schema(description = "The unique identifier for the inventory", example = "1")
    private Long inventoryId;

    @Schema(description = "The total quantity of items in the inventory", example = "100")
    private int total;

    @Schema(description = "The quantity of items available for sale", example = "80")
    private int available;

    @Schema(description = "The quantity of items reserved for orders", example = "20")
    private int reserved;
}
