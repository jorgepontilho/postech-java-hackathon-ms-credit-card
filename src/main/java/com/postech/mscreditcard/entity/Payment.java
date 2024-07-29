package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.PaymentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tb_payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "value", precision = 10, scale = 2, nullable = false)
    private BigDecimal value;


    public Payment(PaymentDTO paymentDTO, Card card) {
        this.id = paymentDTO.getId();
        this.card = card;
        this.customer = card.getCustomer();
        this.value = paymentDTO.getValor();
    }

    public PaymentDTO toDTO() {
        return new PaymentDTO(this);
    }
}
