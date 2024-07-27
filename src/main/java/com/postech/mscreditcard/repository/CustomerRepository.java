package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByCpf(String cpf);
}
