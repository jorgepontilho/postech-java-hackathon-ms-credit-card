package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.*;
import com.postech.mscreditcard.entity.*;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.interfaces.ICreditCardGateway;
import com.postech.mscreditcard.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CreditCardGateway implements ICreditCardGateway {

    @Value("${app.login}")
    public String LOGIN;
    @Value("${app.password}")
    public String PASSWORD;
    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;

    public CreditCardGateway(CreditCardRepository creditCardRepository, CustomerRepository customerRepository) {
        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDTO findByLoginAndPassword(String login, String password) {
        try {
            //Login e usuário definido pelos professores
            if (LOGIN.equals(login) && PASSWORD.equals(password)) {
                return new UserDTO(login, password);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CreditCardDTO createCard(CreditCardDTO creditCardDTO) {
        try {
            CreditCard creditCardNew = new CreditCard(creditCardDTO);
            Customer customer = customerRepository.findByCpf(creditCardDTO.getCpf())
                    .orElseThrow(() -> new NotFoundException("Customer not found"));
            creditCardNew.setCustomer(customer);
            creditCardNew = creditCardRepository.save(creditCardNew);
            return creditCardNew.toDTO();
        } catch (Exception e ){
            log.error("Error creating card", e);
            throw e;
        }

    }

    private CreditCardDTO toCardDTO(CreditCard creditCard) {
        return creditCard.toDTO();
    }

    public List<CreditCardDTO> listAllCards() {
        List<CreditCard> creditCardList = creditCardRepository.findAll();
        return creditCardList
                .stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    public List<CreditCardDTO> listAllCustomerCards(String cpf) {
        try {
            log.info("List all customer cards {}",cpf);
            List<CreditCard> creditCardList = creditCardRepository.findAllByCpf(cpf);
            return creditCardList
                    .stream()
                    .map(this::toCardDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing customer cards", e);
            throw e;
        }
    }
}
