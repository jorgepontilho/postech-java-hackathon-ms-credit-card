package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.PaymentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_Payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String cpf;
    private String numero;
    private String data_validade;
    private String cvv;
    private double valor;

    public Payment(PaymentDTO PaymentDTO) {
        this.id = PaymentDTO.getId();
        this.cpf = PaymentDTO.getCpf();
        this.numero = PaymentDTO.getNumero();
        this.data_validade = PaymentDTO.getData_validade();
        this.cvv = PaymentDTO.getCvv();
        this.valor = PaymentDTO.getValor();
    }

    public PaymentDTO toDTO() {
        return new PaymentDTO(this.id, this.cpf, this.numero, this.data_validade, this.cvv, this.valor);
    }
}
