package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.CreditCardDTO;
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
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "expiration_date")
    private String expirationDate;

    @Column(name = "limit_value")
    private BigDecimal limitValue;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public CreditCard(CreditCardDTO creditCardDTO) {
        this.id = creditCardDTO.getId();
        this.cardNumber = creditCardDTO.getNumero();
        this.cpf = creditCardDTO.getCpf();
        this.expirationDate = creditCardDTO.getDataValidade();
        this.limitValue = creditCardDTO.getLimite();
        this.cvv = creditCardDTO.getCvv();
    }

    public CreditCardDTO toDTO() {
        return new CreditCardDTO(this.id, this.cardNumber, this.cpf, this.cvv,  this.expirationDate, this.limitValue);
    }
}
