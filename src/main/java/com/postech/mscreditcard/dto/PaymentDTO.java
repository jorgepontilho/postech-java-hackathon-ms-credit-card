package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    Integer id;
    @NotNull
    private String cpf;
    @NotNull
    private String numero;
    @NotNull
    private String data_validade;
    @NotNull
    private String cvv;
    @NotNull
    private double valor;
    public Payment toEntity() {
        return new Payment(this);
    }
}