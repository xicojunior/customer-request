package br.com.customer.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
//nome foi traocado por conta de palavra reservada no H2 e testes unitarios.
@Table(name = "Order_")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "numberControl")
    private String numberControl;

    @Column(name = "dateRegistration")
    private LocalDate dateRegistration;

    @Column(name = "productName")
    private String productName;

    @Column(name = "unitValue")
    private BigDecimal unitValue;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "totalValue")
    private BigDecimal totalValue;

    @Column(name = "codeCustomer")
    private Integer codeCustomer;

    @Column(name = "error")
    private String error;

}
