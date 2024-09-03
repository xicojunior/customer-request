package br.com.customer.controller;

import br.com.customer.model.Order;
import br.com.customer.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldReturnCreatedStatus_WhenValidOrdersProvided() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");
        order1.setUnitValue(new BigDecimal("100.50"));
        order1.setAmount(3);

        Order order2 = new Order();
        order2.setNumberControl("12346");
        order2.setUnitValue(new BigDecimal("50.00"));
        order2.setAmount(1);

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.createOrderList(orders)).thenReturn(orders);

        // Act
        ResponseEntity<List<Order>> response = orderController.createOrder(orders);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(orderService, times(1)).createOrderList(orders);
    }

    @Test
    void createOrder_ShouldReturnBadRequestStatus_WhenMoreThan10OrdersProvided() {
        // Arrange
        List<Order> orders = Arrays.asList(new Order[11]);

        // Act
        ResponseEntity<List<Order>> response = orderController.createOrder(orders);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService, never()).createOrderList(orders);
    }

    @Test
    void findOrders_ShouldReturnOrdersBasedOnFilters() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");

        List<Order> orders = Arrays.asList(order1);
        when(orderService.findOrders(anyString(), anyString())).thenReturn(orders);

        // Act
        ResponseEntity<List<Order>> response = orderController.findOrders("12345", "26/08/2024");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).findOrders("12345", "26/08/2024");
    }

    @Test
    void findOrders_ShouldReturnAllOrders_WhenNoFiltersProvided() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");

        List<Order> orders = Arrays.asList(order1);
        when(orderService.findAllOrders()).thenReturn(orders);

        // Act
        ResponseEntity<List<Order>> response = orderController.findOrders(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).findAllOrders();
    }
}
