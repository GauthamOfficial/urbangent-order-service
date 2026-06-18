package com.urbangent.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID productId;
    private String name;
    private String description;
    private String category;
    private BigDecimal unitPrice;
    private Integer stock;
    private String size;
    private String color;
    private String brand;
    private String material;
}
