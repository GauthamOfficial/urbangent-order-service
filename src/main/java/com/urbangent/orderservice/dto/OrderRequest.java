package com.urbangent.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
