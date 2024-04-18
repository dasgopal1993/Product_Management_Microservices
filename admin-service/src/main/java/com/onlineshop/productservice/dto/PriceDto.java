package com.onlineshop.productservice.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {

    @Schema(description = "The unique identifier for the price", example = "1")
    private Long priceId;

    @Schema(description = "The currency of the price", example = "USD")
    private String currency;

    @Schema(description = "The amount of the price", example = "49.99")
    private BigDecimal amount;
}
