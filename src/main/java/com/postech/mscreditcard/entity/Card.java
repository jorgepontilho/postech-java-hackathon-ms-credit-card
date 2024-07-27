package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.CardDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_Card")
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String cpf;
    private double limite;
    private String numero;
    private String data_validade;
    private String cvv;

    public Card(CardDTO CardDTO) {
        this.id = CardDTO.getId();
        this.cpf = CardDTO.getCpf();
        this.limite = CardDTO.getLimite();
        this.numero = CardDTO.getNumero();
        this.data_validade = CardDTO.getData_validade();
        this.cvv = CardDTO.getCvv();
    }

    public CardDTO toDTO() {
        return new CardDTO(this.id, this.cpf, this.limite, this.numero, this.data_validade, this.cvv);
    }
}
