package com.urbangent.orderservice.repository;

import com.urbangent.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

  /**
   * Finds all orders placed by a specific customer.
   *
   * @param customerId the unique identifier of the customer
   * @return list of orders belonging to the given customer
   */
  List<Order> findByCustomerId(String customerId);
}