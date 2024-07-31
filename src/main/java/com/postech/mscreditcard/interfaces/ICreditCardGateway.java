package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.UserDTO;

import java.util.List;

public interface ICreditCardGateway {
    UserDTO findByLoginAndPassword(String login, String password);

    CardDTO createCard(CardDTO cardDTO);

    List<CardDTO> listAllCustomerCards(String cpf);
}
