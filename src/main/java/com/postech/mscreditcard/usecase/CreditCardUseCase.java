package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CreditCardDTO;
import com.postech.mscreditcard.exceptions.MaxCardsException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CreditCardUseCase {

    private final CreditCardGateway creditCardGateway;
    private final int MAX_CARDS = 2;

    public CreditCardUseCase(CreditCardGateway creditCardGateway) {
        this.creditCardGateway = creditCardGateway;
    }

    public void validateCardCreation(CreditCardDTO creditCardDTO) {

        try {
            if (creditCardDTO == null) {
                throw new IllegalArgumentException("Cartão inválido.");
            }
            log.info("Validate card creation {}", creditCardDTO.toString());
            List<CreditCardDTO> creditCreditCardDTOList = creditCardGateway.listAllCustomerCards(creditCardDTO.getCpf());
            if (creditCreditCardDTOList.size() == MAX_CARDS){
                throw new MaxCardsException();
            }
        } catch (UnknownErrorException ue) {
            throw ue;
        }
    }
}
