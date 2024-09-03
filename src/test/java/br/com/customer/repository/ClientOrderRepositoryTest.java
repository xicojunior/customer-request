package br.com.customer.repository;

import br.com.customer.model.ClientOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
class ClientOrderRepositoryTest {

    @Mock
    private ClientOrderRepository clientOrderRepository;

    @InjectMocks
    private ClientOrder clientOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializando um ClientOrder básico para os testes
        clientOrder = new ClientOrder();
        clientOrder.setTotalShipping(BigDecimal.valueOf(100.00));
    }

    @Test
    void testSaveClientOrder() {
        when(clientOrderRepository.save(any(ClientOrder.class))).thenReturn(clientOrder);

        ClientOrder savedClientOrder = clientOrderRepository.save(clientOrder);

        assertNotNull(savedClientOrder);
        assertEquals(BigDecimal.valueOf(100.00), savedClientOrder.getTotalShipping());
        verify(clientOrderRepository, times(1)).save(any(ClientOrder.class));
    }

    @Test
    void testFindById() {
        when(clientOrderRepository.findById(any(Long.class))).thenReturn(Optional.of(clientOrder));

        Optional<ClientOrder> foundClientOrder = clientOrderRepository.findById(1L);

        assertTrue(foundClientOrder.isPresent());
        assertEquals(BigDecimal.valueOf(100.00), foundClientOrder.get().getTotalShipping());
        verify(clientOrderRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteClientOrder() {
        doNothing().when(clientOrderRepository).deleteById(any(Long.class));

        clientOrderRepository.deleteById(1L);

        verify(clientOrderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindAllClientOrders() {
        when(clientOrderRepository.findAll()).thenReturn(List.of(clientOrder));

        var clientOrders = clientOrderRepository.findAll();

        assertFalse(clientOrders.isEmpty());
        assertEquals(1, clientOrders.size());
        assertEquals(BigDecimal.valueOf(100.00), clientOrders.get(0).getTotalShipping());
        verify(clientOrderRepository, times(1)).findAll();
    }
}
