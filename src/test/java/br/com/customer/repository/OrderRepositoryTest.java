package br.com.customer.repository;

import br.com.customer.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        // Cria pedidos para os testes
        order1 = new Order();
        order1.setNumberControl("12345");
        order1.setDateRegistration(LocalDate.now());
        order1.setUnitValue(new BigDecimal("100.50"));
        order1.setAmount(3);
        orderRepository.save(order1);

        order2 = new Order();
        order2.setNumberControl("67890");
        order2.setDateRegistration(LocalDate.now().minusDays(1));
        order2.setUnitValue(new BigDecimal("200.75"));
        order2.setAmount(2);
        orderRepository.save(order2);
    }

    @Test
    void testSaveOrder() {
        // Arrange
        Order newOrder = new Order();
        newOrder.setNumberControl("54321");
        newOrder.setDateRegistration(LocalDate.now());
        newOrder.setUnitValue(new BigDecimal("150.00"));
        newOrder.setAmount(4);

        // Act
        Order savedOrder = orderRepository.save(newOrder);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals("54321", savedOrder.getNumberControl());
        assertEquals(LocalDate.now(), savedOrder.getDateRegistration());
        assertEquals(new BigDecimal("150.00"), savedOrder.getUnitValue());
        assertEquals(4, savedOrder.getAmount());
    }

    @Test
    void testFindById() {
        // Act
        Optional<Order> foundOrder = orderRepository.findById(order1.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order1.getNumberControl(), foundOrder.get().getNumberControl());
    }

    @Test
    void testDeleteOrder() {
        // Act
        orderRepository.deleteById(order1.getId());

        // Assert
        Optional<Order> deletedOrder = orderRepository.findById(order1.getId());
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    void testFindAllOrders() {
        // Act
        List<Order> orders = orderRepository.findAll();

        // Assert
        assertEquals(2, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
    }

    @Test
    void existsByNumberControl_ShouldReturnTrue_WhenNumberControlExists() {
        // Act
        boolean exists = orderRepository.existsByNumberControl("12345");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByNumberControl_ShouldReturnFalse_WhenNumberControlDoesNotExist() {
        // Act
        boolean exists = orderRepository.existsByNumberControl("11111");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByNumberControlOrDateRegistration_ShouldReturnOrdersBasedOnNumberControl() {
        // Act
        List<Order> orders = orderRepository.findByNumberControlOrDateRegistration("12345", null);

        // Assert
        assertEquals(1, orders.size());
        assertEquals("12345", orders.get(0).getNumberControl());
    }

    @Test
    void findByNumberControlOrDateRegistration_ShouldReturnOrdersBasedOnDateRegistration() {
        // Act
        List<Order> orders = orderRepository.findByNumberControlOrDateRegistration(null, LocalDate.now());

        // Assert
        assertEquals(1, orders.size());
        assertEquals(LocalDate.now(), orders.get(0).getDateRegistration());
    }

    @Test
    void findByNumberControlOrDateRegistration_ShouldReturnEmptyList_WhenNoMatchesFound() {
        // Act
        List<Order> orders = orderRepository.findByNumberControlOrDateRegistration("11111", LocalDate.of(2024, 8, 26));

        // Assert
        assertTrue(orders.isEmpty());
    }
}
