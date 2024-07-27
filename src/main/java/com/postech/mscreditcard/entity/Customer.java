package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.CustomerDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_Customer")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private String pais;

    public Customer(CustomerDTO CustomerDTO) {
        this.id = CustomerDTO.getId();
        this.cpf = CustomerDTO.getCpf();
        this.nome = CustomerDTO.getNome();
        this.email = CustomerDTO.getEmail();
        this.telefone = CustomerDTO.getTelefone();
        this.rua = CustomerDTO.getRua();
        this.cidade = CustomerDTO.getRua();
        this.estado = CustomerDTO.getEstado();
        this.cep = CustomerDTO.getCep();
        this.pais = CustomerDTO.getPais();
    }
    public CustomerDTO toDTO() {
        return new CustomerDTO(
                this.id,
                this.cpf,
                this.nome,
                this.email,
                this.telefone,
                this.rua,
                this.cidade,
                this.estado,
                this.cep,
                this.pais
        );
    }
}
