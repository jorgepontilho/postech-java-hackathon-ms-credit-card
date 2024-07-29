package com.postech.mscreditcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CardDTO {
    Integer id;
    @CPF(message = "CPF Inválido")
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Limite é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limite precisa ser maior que 0")
    private BigDecimal limite;

    @NotBlank(message = "Número é obrigatório")
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Formato de  (**** **** **** 1234)")
    private String numero;

    @NotBlank(message = "Data é obrigatório")
    @JsonProperty("data_validade")
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = "Data de validade inválida")
    private String dataValidade;

    @NotBlank(message = "CVV é obrigatório")
    @Size(min = 3, max = 4, message = "CVV tem que ser entre 3 ou 4 digitos")
    private String cvv;

    public CardDTO(Integer id, String cpf, BigDecimal limitValue, String cardNumber, String expirationDate, String cvv) {
        this.id = id;
        this.cpf = cpf;
        this.limite = limitValue;
        this.numero = cardNumber;
        this.dataValidade = expirationDate;
        this.cvv = cvv;
    }
}