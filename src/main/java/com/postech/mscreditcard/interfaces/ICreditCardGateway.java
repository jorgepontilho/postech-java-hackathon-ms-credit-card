package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CreditCardDTO;
import com.postech.mscreditcard.dto.UserDTO;

import java.util.List;

public interface ICreditCardGateway {
    UserDTO findByLoginAndPassword(String login, String password);

    CreditCardDTO createCard(CreditCardDTO creditCardDTO);

    List<CreditCardDTO> listAllCards();
}
