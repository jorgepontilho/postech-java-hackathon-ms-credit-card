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
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;




    public CreditCardGateway(CardRepository cardRepository, PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
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
                    .orElseThrow(() -> new NotFoundException("Customer not found"));
            cardNew.setCustomer(customer);
            cardNew = cardRepository.save(cardNew);
            return cardNew.toDTO();
        } catch (Exception e ){
            log.error("Error creating card", e);
            throw e;
        }

    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment paymentNew = new Payment(paymentDTO);
        paymentNew = paymentRepository.save(paymentNew);
        return paymentNew.toDTO();
    }

    private CardDTO toCardDTO(Card card) {
        return card.toDTO();
    }

    private PaymentDTO toPaymentDTO(Payment payment) {
        return payment.toDTO();
    }

    public List<CardDTO> listAllCards() {
        List<Card> cardList = cardRepository.findAll();
        return cardList
                .stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> listAllPayments() {
        List<Payment> paymentList = paymentRepository.findAll();
        return paymentList
                .stream()
                .map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

    public List<CardDTO> listAllCustomerCards(String cpf) {
        try {
            log.info("List all customer cards {}",cpf);
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
}
