package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.dto.UserDTO;

import java.util.List;

public interface ICreditCardGateway {
    UserDTO findByLoginAndPassword(String login, String password);

    CustomerDTO findByCpf(String cpf);
}
