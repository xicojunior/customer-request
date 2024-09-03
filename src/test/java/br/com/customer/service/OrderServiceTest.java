package br.com.customer.service;

import br.com.customer.model.Order;
import br.com.customer.repository.ClientOrderRepository;
import br.com.customer.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientOrderRepository clientOrderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //Teste Criando nova lista
    void createOrderList_ShouldCreateOrdersAndReturnNewList() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");
        order1.setUnitValue(new BigDecimal("100.50"));
        order1.setAmount(3);

        Order order2 = new Order();
        order2.setNumberControl("12346");
        order2.setUnitValue(new BigDecimal("50.00"));
        order2.setAmount(1);

        when(orderRepository.existsByNumberControl("12345")).thenReturn(false);
        when(orderRepository.existsByNumberControl("12346")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Order> listOrder = Arrays.asList(order1, order2);

        // Act
        List<Order> result = orderService.createOrderList(listOrder);

        // Assert
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getTotalValue());
        assertNotNull(result.get(1).getTotalValue());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    //Teste numero de controle duplicado
    void createOrder_ShouldReturnError_WhenNumberControlExists() {
        // Arrange
        Order order = new Order();
        order.setNumberControl("12345");

        when(orderRepository.existsByNumberControl("12345")).thenReturn(true);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals("Número de controle já existe.", result.getError());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    //Teste valroes padrão (data e quantidade) sao definidos corretamente se nulos
    void createOrder_ShouldSetDefaultValues_WhenFieldsAreNull() {
        // Arrange
        Order order = new Order();
        order.setNumberControl("12345");
        order.setUnitValue(new BigDecimal("100.00"));

        when(orderRepository.existsByNumberControl("12345")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals(1, result.getAmount());
        assertEquals(LocalDate.now(), result.getDateRegistration());
        assertEquals(new BigDecimal("100.00"), result.getTotalValue());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    //Testa os descontos de 10% e 5% dependendo das quantidades
    void createOrder_ShouldApplyDiscounts_WhenAmountIsGreaterThan5Or10() {
        // Arrange
        Order order = new Order();
        order.setNumberControl("12345");
        order.setUnitValue(new BigDecimal("100.00"));
        order.setAmount(10);

        when(orderRepository.existsByNumberControl("12345")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals(new BigDecimal("900.00"), result.getTotalValue()); // 10% de desconto
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    //testa a busca pelo numero de controle ou pela data
    void findOrders_ShouldReturnOrdersBasedOnNumberControlOrDate() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");
        order1.setDateRegistration(LocalDate.of(2024, 8, 26));

        when(orderRepository.findByNumberControlOrDateRegistration(anyString(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(order1));

        // Act
        List<Order> result = orderService.findOrders("12345", "26/08/2024");

        // Assert
        assertEquals(1, result.size());
        verify(orderRepository).findByNumberControlOrDateRegistration(anyString(), any(LocalDate.class));
    }

    @Test
    //testa retornar todos
    void findAllOrders_ShouldReturnAllOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setNumberControl("12345");

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1));

        // Act
        List<Order> result = orderService.findAllOrders();

        // Assert
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }
}
