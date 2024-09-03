package br.com.customer.repository;

import br.com.customer.model.ClientOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {
}
