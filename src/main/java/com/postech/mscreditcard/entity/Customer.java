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
    private Long id;
    @Column(unique = true, nullable = false)
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private String rua;
    private String cidade;
    private String estado;
    private String cep;
    private String pais;

    public Customer(CustomerDTO customerDTO) {
        this.id = customerDTO.getId();
        this.cpf = customerDTO.getCpf();
        this.nome = customerDTO.getNome();
        this.email = customerDTO.getEmail();
        this.telefone = customerDTO.getTelefone();
        this.rua = customerDTO.getRua();
        this.cidade = customerDTO.getRua();
        this.estado = customerDTO.getEstado();
        this.cep = customerDTO.getCep();
        this.pais = customerDTO.getPais();
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
