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
    Integer id;
    private String cpf;
    private BigDecimal limitValue;
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    @ManyToOne
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
