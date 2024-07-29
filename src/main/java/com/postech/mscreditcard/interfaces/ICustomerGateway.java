package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CustomerDTO;

public interface ICustomerGateway {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO findByCpf(String cpf);
}
