package br.com.customer.service;

import br.com.customer.model.ClientOrder;
import br.com.customer.model.Order;
import br.com.customer.repository.ClientOrderRepository;
import br.com.customer.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(OrderService.class.getName());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    private BigDecimal totalClientOrder = BigDecimal.ZERO;
    private BigDecimal totalShipping = BigDecimal.ZERO;

    public List<Order> createOrderList(List<Order> listOrder) {

        List<Order> newListOrder = new ArrayList<>();

        for (Order order : listOrder) {
            Order newOrder = new Order();
            newOrder = createOrder(order);
            if ( newOrder.getTotalValue() != null) {
                totalShipping = totalClientOrder.add(newOrder.getTotalValue());
                this.createClietOrder();
            }
            newListOrder.add(newOrder);
        }

        return newListOrder;
    }

    public ClientOrder createClietOrder() {

        ClientOrder clientOrder = new ClientOrder();

        try {
            clientOrder.setTotalShipping(totalShipping);
            clientOrderRepository.save(clientOrder);
        }catch (Exception e) {
               e.printStackTrace();
        }
        return clientOrder;
    }

    public Order createOrder(Order order) {

        //verifica registro duplicado tratando controle exitente e sinalizando no campo de erro para continuar a execução.
        boolean duplicateOrder = checkExistOrder(order);
        if ( duplicateOrder ) {
            return  order;
        }

        // Setando data de cadastro atual, se não informada
        setDefaultDate(order);

        // Definindo a quantidade padrão como 1, se não informado
        setAmountDefault(order);

        // Calculando o valor total do produto
        BigDecimal totalValue = calculateTotalProduct(order);

        //para 10 unidades ou acima desconto de 10%
        totalValue = applyDiscounts(order, totalValue);

        log.info("valorTotal = " + totalValue);
        order.setTotalValue(totalValue);

        return orderRepository.save(order);
    }

    //Aplica a politica de descontos
    private BigDecimal applyDiscounts(Order order, BigDecimal totalValue) {
        if (order.getAmount() >= 10) {
            totalValue = totalValue.multiply(BigDecimal.valueOf(0.9)).setScale(2, RoundingMode.HALF_UP);
            log.info("valorTotal => 10 -> " + totalValue);

            //para acima de 5 unidades desconto de 5%
        } else if (order.getAmount() > 5) {
            totalValue = totalValue.multiply(BigDecimal.valueOf(0.95)).setScale(2, RoundingMode.HALF_UP);
            log.info("valorTotal > 5 -> " + totalValue);
        }
        return totalValue;
    }

    //calcula o total do produto
    private BigDecimal calculateTotalProduct(Order order) {
        BigDecimal totalValue = order.getUnitValue().multiply(BigDecimal.valueOf(order.getAmount()));
        return totalValue;
    }

    //seta o valor padrão para 1 se vier nulo
    private void setAmountDefault(Order order) {
        if (order.getAmount() == null) {
            order.setAmount(1);
        }
    }

    //verifica registro sem data e seta uma data padrao
    private void setDefaultDate(Order order) {
        if (order.getDateRegistration() == null) {
            order.setDateRegistration(LocalDate.now());
            log.info("DateRegistration = " + LocalDate.now());
        }
    }

    //verifica registro duplicado
    private boolean checkExistOrder(Order order) {

        boolean check = false;
        try {
            if (orderRepository.existsByNumberControl(order.getNumberControl())) {
                log.info("Número de controle já existe. Não inserido no banco de dados");
                order.setError("Número de controle já existe. Não inserido no banco de dados");
                check = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return check;
    }

    public List<Order> findOrders(String numberControl, String dateRegistration) {
        List<Order> orders;
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateRegistration, parser);
        orders = orderRepository.findByNumberControlOrDateRegistration(numberControl, date);
        return orders;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }


}
