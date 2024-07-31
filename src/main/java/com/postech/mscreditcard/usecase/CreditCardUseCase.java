package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.exceptions.CardExistException;
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

    public void validateCardCreation(CardDTO cardDTO) {
        try {
            if (cardDTO == null) {
                throw new IllegalArgumentException("Cartão inválido.");
            }
            log.info("Validate card creation {}", cardDTO.toString());
            List<CardDTO> creditCardDTOList = creditCardGateway.listAllCustomerCards(cardDTO.getCpf());
            if (creditCardDTOList.size() == MAX_CARDS){
                throw new MaxCardsException("Máximo de cartões atingido");
            }

            creditCardDTOList = creditCardGateway.listAllCards(cardDTO.getNumero());
            if (!creditCardDTOList.isEmpty()){
                throw new CardExistException("Número de Cartão já existe.");
            }

        } catch (UnknownErrorException ue) {
            throw ue;
        }
    }
}
