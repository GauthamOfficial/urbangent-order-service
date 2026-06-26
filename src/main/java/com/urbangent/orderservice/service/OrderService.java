package com.urbangent.orderservice.service;

import com.urbangent.orderservice.dto.OrderRequest;
import com.urbangent.orderservice.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the order UUID
     * @return the order details
     */
    OrderResponse getOrderById(UUID id);

    /**
     * Retrieves all orders in the system.
     *
     * @return list of all orders
     */
    List<OrderResponse> getAllOrders();

    /**
     * Retrieves all orders placed by a specific customer.
     *
     * @param customerId the customer identifier
     * @return list of orders for the customer
     */
    List<OrderResponse> getOrdersByCustomerId(String customerId);
}