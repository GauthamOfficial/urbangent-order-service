package com.urbangent.orderservice.dto;

import com.urbangent.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private UUID orderId;
    private String customerId;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private OrderStatus status;
}
