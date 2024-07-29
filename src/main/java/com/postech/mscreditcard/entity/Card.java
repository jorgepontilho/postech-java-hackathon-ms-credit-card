package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.CardDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tb_card")
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", nullable = false)
    private String cpf;
    @Column(name = "limit_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal limitValue;
    @Column(name = "card_number", nullable = false)
    private String cardNumber;
    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;
    @Column(name = "cvv", nullable = false)
    private String cvv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Card(CardDTO cardDTO) {
        this.id = cardDTO.getId();
        this.cpf = cardDTO.getCpf();
        this.limitValue = cardDTO.getLimite();
        this.cardNumber = cardDTO.getNumero();
        this.expirationDate = cardDTO.getDataValidade();
        this.cvv = cardDTO.getCvv();
    }

    public CardDTO toDTO() {
        return new CardDTO(this.id, this.cpf, this.limitValue, this.cardNumber, this.expirationDate, this.cvv);
    }
}
