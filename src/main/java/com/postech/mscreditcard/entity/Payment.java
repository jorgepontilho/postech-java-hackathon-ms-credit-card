package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.PaymentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "value", precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public Payment(PaymentDTO paymentDTO, Card card) {
        this.id = paymentDTO.getId();
        this.uuid = UUID.randomUUID().toString();
        this.card = card;
        this.customer = card.getCustomer();
        this.value = paymentDTO.getValor();
    }

    public PaymentDTO toDTO() {
        return new PaymentDTO(this);
    }
}
