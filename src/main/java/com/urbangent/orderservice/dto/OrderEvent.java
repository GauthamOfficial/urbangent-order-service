package com.urbangent.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent implements Serializable {

    private UUID orderId;
    private String customerId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
}
