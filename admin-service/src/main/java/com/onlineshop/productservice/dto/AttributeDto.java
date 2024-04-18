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
public class AttributeDto {

    @Schema(description = "The unique identifier for the attribute", example = "1")
    private Long attributeId;

    @Schema(description = "The name of the attribute", example = "Color")
    private String name;

    @Schema(description = "The value of the attribute", example = "Red")
    private String value;
}
