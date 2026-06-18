package com.urbangent.orderservice.service;

import com.urbangent.orderservice.dto.OrderRequest;
import com.urbangent.orderservice.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);
}
