package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.UserDTO;

public interface ICreditCardGateway {
    UserDTO findByLoginAndPassword(String login, String password);

    CardDTO createCard(CardDTO cardDTO);
}
