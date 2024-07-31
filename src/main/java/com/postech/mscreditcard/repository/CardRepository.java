package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findAllByCpf(String cpf);
    Optional<Card> findByCardNumber(String cardNumber);
    Card findByCpfAndCardNumber(String cpf, String cardNumber);
}
