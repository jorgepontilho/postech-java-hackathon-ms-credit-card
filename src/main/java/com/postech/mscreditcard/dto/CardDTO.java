package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Card;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    Integer id;
    @NotNull
    private String cpf;
    @NotNull
    private double limite;
    @NotNull
    private String numero;
    @NotNull
    private String data_validade;
    @NotNull
    private String cvv;
    public Card toEntity() {
        return new Card(this);
    }
}