package br.com.customer.controller;

import br.com.customer.model.Order;
import br.com.customer.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<List<Order>> createOrder(@RequestBody List<Order> orders) {
        if (orders.size() > 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<Order> newListOrder = orderService.createOrderList(orders);

        return ResponseEntity.status(HttpStatus.CREATED).body(newListOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> findOrders(
            @RequestParam(required = false) String numberControl,
            @RequestParam(required = false) String dateRegistration) {

        List<Order> orders;
        if (numberControl != null || dateRegistration != null) {
            orders = orderService.findOrders(numberControl, dateRegistration);
        } else {
            orders = orderService.findAllOrders();
        }

        return ResponseEntity.ok(orders);
    }

}
