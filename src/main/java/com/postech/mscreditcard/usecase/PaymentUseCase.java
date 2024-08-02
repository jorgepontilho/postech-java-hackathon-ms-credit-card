package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.CardNotFoundException;
import com.postech.mscreditcard.exceptions.InvalidPaymentException;
import com.postech.mscreditcard.exceptions.NoLimitCardException;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import com.postech.mscreditcard.gateway.PaymentGateway;
import com.postech.mscreditcard.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class PaymentUseCase {

    private final CustomerRepository customerRepository;
    private final PaymentGateway paymentGateway;
    private final CreditCardGateway creditCardGateway;
    private static final DateTimeFormatter EXPIRATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");


    public PaymentUseCase(CustomerRepository customerRepository, PaymentGateway paymentGateway, CreditCardGateway creditCardGateway) {
        this.customerRepository = customerRepository;
        this.paymentGateway = paymentGateway;
        this.creditCardGateway = creditCardGateway;
    }

    public void validatePayment(PaymentDTO paymentDTO) {

        try {
            log.info("Validate payment creation {} - {}", paymentDTO.getCpf(), paymentDTO.getValor());
            CardDTO creditCardDTO = creditCardGateway.findCustomerCard(paymentDTO.getCpf(),paymentDTO.getNumero());
            if (creditCardDTO == null){
                throw new CardNotFoundException("Cartão inválido");
            }

            if (!isValidExpirationDate(paymentDTO.getDataValidade())) {
                throw new InvalidPaymentException("Cartão expirado");
            }
            CardDTO cardDTO = creditCardGateway.listAllCustomerCards(paymentDTO.getCpf()).stream()
                    .filter(c -> c.getNumero().equals(paymentDTO.getNumero()))
                    .findFirst()
                    .orElseThrow(() -> new CardNotFoundException("Cartão não encontrado"));

            if (cardDTO.getLimite().compareTo(paymentDTO.getValor()) < 0) {
                log.error("Cartão sem limite: {} cpf: {}", cardDTO.getLimite(), paymentDTO.getCpf());
                throw new NoLimitCardException("Cartão sem limite");
            }

        } catch (Exception e){
            log.error("Error validating payment {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean isValidExpirationDate(String expirationDate) {
        try {
            YearMonth expiration = YearMonth.parse(expirationDate, EXPIRATION_DATE_FORMATTER);
            return expiration.isAfter(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
    }

}
