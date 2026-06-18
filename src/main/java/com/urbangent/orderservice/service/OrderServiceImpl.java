package com.urbangent.orderservice.service;

import com.urbangent.orderservice.dto.*;
import com.urbangent.orderservice.entity.Order;
import com.urbangent.orderservice.entity.OrderStatus;
import com.urbangent.orderservice.exception.OrderCreationException;
import com.urbangent.orderservice.exception.ProductNotFoundException;
import com.urbangent.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${product-service.url}")
    private String productServiceUrl;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        ProductResponse product;
        try {
            product = restTemplate.getForObject(
                    productServiceUrl + "/api/products/" + request.getProductId(),
                    ProductResponse.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Product not found with id: " + request.getProductId());
        } catch (Exception e) {
            throw new OrderCreationException("Failed to fetch product details: " + e.getMessage());
        }

        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + request.getProductId());
        }

        BigDecimal totalPrice = product.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);

        OrderEvent event = OrderEvent.builder()
                .orderId(saved.getOrderId())
                .customerId(saved.getCustomerId())
                .productName(saved.getProductName())
                .quantity(saved.getQuantity())
                .totalPrice(saved.getTotalPrice())
                .orderDate(saved.getOrderDate())
                .build();

        try {
            rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
            log.info("Order event published for order: {}", saved.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish order event: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }
}
