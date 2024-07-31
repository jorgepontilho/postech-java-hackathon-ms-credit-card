package com.postech.mscreditcard.dto;

import com.postech.mscreditcard.entity.Customer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Data
@NoArgsConstructor
public class CustomerDTO {
    Long id;
    @CPF(message = "CPF Inválido")
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome precisa ter entre 2 e 100 letras")
    private String nome;
    @Email(message = "Formato de email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
    @NotBlank(message = "Rua é obrigatório")
    private String rua;
    @NotBlank(message = "Cidade é obrigatório")
    private String cidade;
    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Apenas UF (ex. SP)")
    private String estado;
    @NotBlank(message = "CEP é obrigatório")
    private String cep;
    @NotBlank(message = "País é obrigatório")
    private String pais;

    public CustomerDTO(Long id, String cpf, String name, String email, String phone, String street, String city, String uf, String zipCode, String country) {
        this.id = id;
        this.cpf = cpf;
        this.nome = name;
        this.email = email;
        this.telefone = phone;
        this.rua = street;
        this.cidade = city;
        this.estado = uf;
        this.cep = zipCode;
        this.pais = country;
    }

    public Customer toEntity() {
        return new Customer(this);
    }
}