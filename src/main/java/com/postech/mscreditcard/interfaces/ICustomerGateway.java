package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CustomerDTO;

import java.util.List;

public interface ICustomerGateway {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO findByCpf(String cpf);
    void deleteById(Integer id);
    List<CustomerDTO> listAllCustomers();
}
