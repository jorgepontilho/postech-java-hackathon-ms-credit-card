package com.postech.mscreditcard.entity;

import com.postech.mscreditcard.dto.CustomerDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "tb_customer")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String cpf;
    private String name;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String uf;
    private String zipCode;
    private String country;
    @OneToMany
    private List<Card> cards;

    public Customer(CustomerDTO customerDTO) {
        this.id = customerDTO.getId();
        this.cpf = customerDTO.getCpf();
        this.name = customerDTO.getNome();
        this.email = customerDTO.getEmail();
        this.phone = customerDTO.getTelefone();
        this.street = customerDTO.getRua();
        this.city = customerDTO.getRua();
        this.uf = customerDTO.getEstado();
        this.zipCode = customerDTO.getCep();
        this.country = customerDTO.getPais();
    }
    public CustomerDTO toDTO() {
        return new CustomerDTO(
                this.id,
                this.cpf,
                this.name,
                this.email,
                this.phone,
                this.street,
                this.city,
                this.uf,
                this.zipCode,
                this.country
        );
    }
}
