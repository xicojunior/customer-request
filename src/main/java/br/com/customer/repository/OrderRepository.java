package br.com.customer.repository;

import br.com.customer.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByNumberControl(String numberControl);
    List<Order> findByNumberControlOrDateRegistration(String numberControl, LocalDate dateRegistration);
}
