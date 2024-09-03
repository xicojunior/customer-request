package br.com.customer.service;

import br.com.customer.model.ClientOrder;
import br.com.customer.model.Order;
import br.com.customer.repository.ClientOrderRepository;
import br.com.customer.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.text.DateFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testCreateOrder() {
        Order order = new Order();
        order.setNumberControl("12345");
        order.setUnitValue(BigDecimal.valueOf(100.50));
        order.setAmount(3);
        order.setDateRegistration(null); // Not setting date to test default date setting

        when(orderRepository.existsByNumberControl(any(String.class))).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order savedOrder = orderService.createOrder(order);

        assertNotNull(savedOrder);
        assertEquals("12345", savedOrder.getNumberControl());
        assertNotNull(savedOrder.getDateRegistration());
        assertEquals(BigDecimal.valueOf(301.50), savedOrder.getTotalValue());
    }

    @Test
    void testCreateOrderWithDiscount() {
        Order order = new Order();
        order.setNumberControl("12346");
        order.setUnitValue(BigDecimal.valueOf(100.50));
        order.setAmount(10);

        when(orderRepository.existsByNumberControl(any(String.class))).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order savedOrder = orderService.createOrder(order);

        assertNotNull(savedOrder);
        assertEquals("12346", savedOrder.getNumberControl());
        assertEquals(BigDecimal.valueOf(904.50).setScale(2, RoundingMode.HALF_UP), savedOrder.getTotalValue()); // 10% discount applied
    }

    @Test
    void testCreateOrderWithDuplicateNumberControl() {
        Order order = new Order();
        order.setNumberControl("12347");
        order.setUnitValue(BigDecimal.valueOf(50.00));
        order.setAmount(5);

        when(orderRepository.existsByNumberControl("12347")).thenReturn(true);

        Order result = orderService.createOrder(order);

        assertNull(result.getTotalValue()); // The order should not be processed
        assertEquals("Número de controle já existe. Não inserido no banco de dados", result.getError());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderList() {
        Order order1 = new Order();
        order1.setNumberControl("12348");
        order1.setUnitValue(BigDecimal.valueOf(20.00));
        order1.setAmount(2);

        Order order2 = new Order();
        order2.setNumberControl("12349");
        order2.setUnitValue(BigDecimal.valueOf(30.00));
        order2.setAmount(1);

        when(orderRepository.existsByNumberControl(any(String.class))).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Order> orders = Arrays.asList(order1, order2);
        List<Order> savedOrders = orderService.createOrderList(orders);

        assertEquals(2, savedOrders.size());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void testFindOrders() {
        Order order = new Order();
        order.setNumberControl("12350");
        order.setDateRegistration(LocalDate.now());

        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String strToday = today.format(dateTimeFormatter);

        order.setUnitValue(BigDecimal.valueOf(100.00));
        order.setAmount(5);

        when(orderRepository.findByNumberControlOrDateRegistration(any(String.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(order));

        List<Order> orders = orderService.findOrders("12350", strToday);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("12350", orders.get(0).getNumberControl());
    }

    @Test
    void testFindAllOrders() {
        Order order1 = new Order();
        order1.setNumberControl("12351");

        Order order2 = new Order();
        order2.setNumberControl("12352");

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.findAllOrders();

        assertEquals(2, orders.size());
    }

    @Test
    void testCreateClientOrder() {
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setTotalShipping(BigDecimal.valueOf(50.00));

        when(clientOrderRepository.save(any(ClientOrder.class))).thenAnswer(i -> i.getArguments()[0]);

        ClientOrder savedClientOrder = orderService.createClietOrder();

        assertNotNull(savedClientOrder);
        verify(clientOrderRepository, times(1)).save(any(ClientOrder.class));
    }
}
