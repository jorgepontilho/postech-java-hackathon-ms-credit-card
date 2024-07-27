package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    Integer id;
    @NotNull
    private String cpf;
    @NotNull
    private String nome;
    @NotNull
    private String email;
    private String telefone;
    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private String pais;
    public Customer toEntity() {
        return new Customer(this);
    }
}