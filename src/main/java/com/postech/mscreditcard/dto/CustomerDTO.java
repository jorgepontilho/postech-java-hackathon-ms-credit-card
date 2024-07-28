package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Customer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    Integer id;
    @CPF(message = "Invalid CPF")
    @NotBlank(message = "CPF is required")
    private String cpf;
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String nome;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Phone is required")
    private String telefone;
    @NotBlank(message = "Street is required")
    private String rua;
    @NotBlank(message = "City is required")
    private String cidade;
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must be 2 characters (e.g., SP)")
    private String estado;
    @NotBlank(message = "CEP is required")
    private String cep;
    @NotBlank(message = "Country is required")
    private String pais;
    public Customer toEntity() {
        return new Customer(this);
    }
}