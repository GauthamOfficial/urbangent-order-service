package com.urbangent.orderservice.service;

import com.urbangent.orderservice.dto.OrderEvent;
import com.urbangent.orderservice.dto.OrderRequest;
import com.urbangent.orderservice.dto.OrderResponse;
import com.urbangent.orderservice.dto.ProductResponse;
import com.urbangent.orderservice.entity.Order;
import com.urbangent.orderservice.entity.OrderStatus;
import com.urbangent.orderservice.exception.ProductNotFoundException;
import com.urbangent.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_Success() {
        ReflectionTestUtils.setField(orderService, "productServiceUrl", "http://localhost:8081");

        UUID productId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .customerId("CUST-001")
                .productId(productId)
                .quantity(2)
                .build();

        ProductResponse product = ProductResponse.builder()
                .productId(productId)
                .name("Slim Fit Oxford Shirt")
                .unitPrice(new BigDecimal("39.99"))
                .stock(120)
                .build();

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class))).thenReturn(product);

        Order savedOrder = Order.builder()
                .orderId(UUID.randomUUID())
                .customerId("CUST-001")
                .productId(productId)
                .productName("Slim Fit Oxford Shirt")
                .quantity(2)
                .totalPrice(new BigDecimal("79.98"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals("CUST-001", response.getCustomerId());
        assertEquals("Slim Fit Oxford Shirt", response.getProductName());
        assertEquals(new BigDecimal("79.98"), response.getTotalPrice());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(2, response.getQuantity());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("order.exchange"), eq("order.created"), any(OrderEvent.class));
    }

    @Test
    void createOrder_ProductNotFound() {
        ReflectionTestUtils.setField(orderService, "productServiceUrl", "http://localhost:8081");

        UUID productId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .customerId("CUST-001")
                .productId(productId)
                .quantity(1)
                .build();

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_NullProductResponse() {
        ReflectionTestUtils.setField(orderService, "productServiceUrl", "http://localhost:8081");

        UUID productId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .customerId("CUST-002")
                .productId(productId)
                .quantity(1)
                .build();

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class))).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void getOrderById_ExistingId_ReturnsOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .orderId(orderId)
                .customerId("CUST-001")
                .productId(UUID.randomUUID())
                .productName("Slim Fit Oxford Shirt")
                .quantity(2)
                .totalPrice(new BigDecimal("79.98"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(orderId);

        assertNotNull(response);
        assertEquals("CUST-001", response.getCustomerId());
        assertEquals("Slim Fit Oxford Shirt", response.getProductName());
        assertEquals(new BigDecimal("79.98"), response.getTotalPrice());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_NonExistingId_ThrowsException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getAllOrders_ReturnsListOfOrders() {
        Order order1 = Order.builder()
                .orderId(UUID.randomUUID())
                .customerId("CUST-001")
                .productId(UUID.randomUUID())
                .productName("Oxford Shirt")
                .quantity(1)
                .totalPrice(new BigDecimal("39.99"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order order2 = Order.builder()
                .orderId(UUID.randomUUID())
                .customerId("CUST-002")
                .productId(UUID.randomUUID())
                .productName("Formal Suit")
                .quantity(1)
                .totalPrice(new BigDecimal("249.99"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CONFIRMED)
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<OrderResponse> responses = orderService.getAllOrders();

        assertEquals(2, responses.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrdersByCustomerId_ReturnsFilteredOrders() {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .customerId("CUST-001")
                .productId(UUID.randomUUID())
                .productName("Leather Derby Shoes")
                .quantity(1)
                .totalPrice(new BigDecimal("129.99"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findByCustomerId("CUST-001")).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getOrdersByCustomerId("CUST-001");

        assertEquals(1, responses.size());
        assertEquals("CUST-001", responses.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId("CUST-001");
    }
}