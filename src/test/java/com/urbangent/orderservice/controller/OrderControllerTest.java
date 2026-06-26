package com.urbangent.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbangent.orderservice.dto.OrderRequest;
import com.urbangent.orderservice.dto.OrderResponse;
import com.urbangent.orderservice.entity.OrderStatus;
import com.urbangent.orderservice.exception.ProductNotFoundException;
import com.urbangent.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = OrderController.class, excludeAutoConfiguration = RabbitAutoConfiguration.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_ValidData_Returns201() throws Exception {
        UUID productId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .customerId("CUST-001")
                .productId(productId)
                .quantity(2)
                .build();

        OrderResponse response = OrderResponse.builder()
                .orderId(UUID.randomUUID())
                .customerId("CUST-001")
                .productId(productId)
                .productName("Slim Fit Oxford Shirt")
                .quantity(2)
                .totalPrice(new BigDecimal("79.98"))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("CUST-001"))
                .andExpect(jsonPath("$.productName").value("Slim Fit Oxford Shirt"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_MissingCustomerId_Returns400() throws Exception {
        String invalidJson = """
                {
                    "customerId": "",
                    "productId": "%s",
                    "quantity": 1
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_ProductNotFound_Returns404() throws Exception {
        UUID productId = UUID.randomUUID();
        OrderRequest request = OrderRequest.builder()
                .customerId("CUST-001")
                .productId(productId)
                .quantity(1)
                .build();

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    @Test
    void getOrderById_Found_Returns200() throws Exception {
    UUID orderId = UUID.randomUUID();
    OrderResponse response = OrderResponse.builder()
            .orderId(orderId)
            .customerId("CUST-001")
            .productName("Oxford Shirt")
            .quantity(2)
            .totalPrice(new BigDecimal("79.98"))
            .status(OrderStatus.PENDING)
            .build();

    when(orderService.getOrderById(orderId)).thenReturn(response);

    mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value("CUST-001"))
            .andExpect(jsonPath("$.productName").value("Oxford Shirt"));
}

    @Test
    void getOrderById_Found_Returns200() throws Exception {
    UUID orderId = UUID.randomUUID();
    OrderResponse response = OrderResponse.builder()
            .orderId(orderId)
            .customerId("CUST-001")
            .productName("Oxford Shirt")
            .quantity(2)
            .totalPrice(new BigDecimal("79.98"))
            .status(OrderStatus.PENDING)
            .build();

    when(orderService.getOrderById(orderId)).thenReturn(response);

    mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value("CUST-001"))
            .andExpect(jsonPath("$.productName").value("Oxford Shirt"));
}

    @Test
    void getAllOrders_Returns200() throws Exception {
    OrderResponse response = OrderResponse.builder()
            .orderId(UUID.randomUUID())
            .customerId("CUST-001")
            .productName("Oxford Shirt")
            .quantity(1)
            .totalPrice(new BigDecimal("39.99"))
            .status(OrderStatus.PENDING)
            .build();

    when(orderService.getAllOrders()).thenReturn(List.of(response));

    mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
}

    @Test
    void getOrdersByCustomerId_Returns200() throws Exception {
    OrderResponse response = OrderResponse.builder()
            .orderId(UUID.randomUUID())
            .customerId("CUST-001")
            .productName("Leather Shoes")
            .quantity(1)
            .totalPrice(new BigDecimal("129.99"))
            .status(OrderStatus.PENDING)
            .build();

    when(orderService.getOrdersByCustomerId("CUST-001")).thenReturn(List.of(response));

    mockMvc.perform(get("/api/orders/customer/{customerId}", "CUST-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerId").value("CUST-001"));
}
}
