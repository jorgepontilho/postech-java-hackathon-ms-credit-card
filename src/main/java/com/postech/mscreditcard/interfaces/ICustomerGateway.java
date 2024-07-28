package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.dto.UserDTO;

public interface ICustomerGateway {

    CustomerDTO findByCpf(String cpf);
}
