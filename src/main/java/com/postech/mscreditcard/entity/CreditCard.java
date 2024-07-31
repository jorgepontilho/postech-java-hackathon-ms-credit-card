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
    private String cpf;
    private BigDecimal limitValue;
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    @ManyToOne
    private Customer customer;

    public CreditCard(CreditCardDTO creditCardDTO) {
        this.id = creditCardDTO.getId();
        this.cpf = creditCardDTO.getCpf();
        this.limitValue = creditCardDTO.getLimite();
        this.cardNumber = creditCardDTO.getNumero();
        this.expirationDate = creditCardDTO.getDataValidade();
        this.cvv = creditCardDTO.getCvv();
    }

    public CreditCardDTO toDTO() {
        return new CreditCardDTO(this.id, this.cpf, this.limitValue, this.cardNumber, this.expirationDate, this.cvv);
    }
}
