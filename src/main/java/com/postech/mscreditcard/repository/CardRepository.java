package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

}
