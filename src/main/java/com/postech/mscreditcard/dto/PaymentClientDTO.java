package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentClientDTO {
    private BigDecimal valor;
    private String descricao;
    private String metodo_pagamento;
    private String status;

    public PaymentClientDTO(Payment payment) {
        this.valor = payment.getValue();
        this.descricao = payment.getDescricao();
        this.metodo_pagamento = payment.getMetodo_pagamento();
        this.status = payment.getStatus();
    }
}