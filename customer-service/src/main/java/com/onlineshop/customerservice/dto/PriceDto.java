package com.onlineshop.customerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {

    private Long priceId;

    private String currency;

    private BigDecimal amount;
}
