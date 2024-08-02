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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CreditCardGateway implements ICreditCardGateway {

    @Value("${app.login}")
    public String LOGIN;
    @Value("${app.password}")
    public String PASSWORD;
    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;


    public CreditCardGateway(CardRepository cardRepository, CustomerRepository customerRepository) {
        this.cardRepository = cardRepository;
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
    public CardDTO createCard(CardDTO cardDTO) {
        try {
            Card cardNew = new Card(cardDTO);
            Customer customer = customerRepository.findByCpf(cardDTO.getCpf())
                    .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
            cardNew.setCustomer(customer);
            cardNew = cardRepository.save(cardNew);
            return cardNew.toDTO();
        } catch (Exception e) {
            log.error("Error creating card", e);
            throw e;
        }

    }

    private CardDTO toCardDTO(Card card) {
        return card.toDTO();
    }

    public List<CardDTO> listAllCards() {
        List<Card> cardList = cardRepository.findAll();
        return cardList
                .stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardDTO> listAllCustomerCards(String cpf) {
        try {
            log.info("List all customer cards {}", cpf);
            List<Card> cardList = cardRepository.findAllByCpf(cpf);
            return cardList
                    .stream()
                    .map(this::toCardDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing customer cards", e);
            throw e;
        }
    }

    @Override
    public CardDTO findCustomerCard(String cpf, String cardNumber) {
        try {
            log.info("Find customer card {}", cpf);
            CardDTO cardDto = null;
            Card card = cardRepository.findByCpfAndCardNumber(cpf, cardNumber);
            if (card != null) {
                cardDto = card.toDTO();
            }
            return cardDto;
        } catch (Exception e) {
            log.error("Error find customer card", e);
            throw e;
        }
    }

    @Override
    public List<CardDTO> listAllCards(String number) {
        try {
            log.info("List all cards {}", number);
            Optional<Card> cardList = cardRepository.findByCardNumber(number);
            return cardList
                    .stream()
                    .map(this::toCardDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing cards", e);
            throw e;
        }
    }
}
