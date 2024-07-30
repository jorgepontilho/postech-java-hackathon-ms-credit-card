package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.dto.UserDTO;

import java.util.List;

public interface ICustomerGateway {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO findByCpf(String cpf);
    void deleteById(Long id);
    List<CustomerDTO> listAllCustomers();
}
