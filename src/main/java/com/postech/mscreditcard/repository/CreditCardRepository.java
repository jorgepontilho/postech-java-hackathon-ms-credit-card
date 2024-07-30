package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    List<CreditCard> findAllByCpf(String cpf);
}
