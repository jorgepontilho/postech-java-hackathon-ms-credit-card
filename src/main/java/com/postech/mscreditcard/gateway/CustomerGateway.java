package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.interfaces.ICustomerGateway;
import com.postech.mscreditcard.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerGateway implements ICustomerGateway {
    private final CustomerRepository customerRepository;


    public CustomerGateway(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customerNew = new Customer(customerDTO);
        customerNew = customerRepository.save(customerNew);
        return customerNew.toDTO();
    }

    @Override
    public CustomerDTO findByCpf(String cpf) {
        try {
            return customerRepository.findByCpf(cpf).toDTO();
        } catch (Exception e) {
            return null;
        }
    }



    private CustomerDTO toCustomerDTO(Customer customer) {
        return customer.toDTO();
    }



    public List<CustomerDTO> listAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        return customerList
                .stream()
                .map(this::toCustomerDTO)
                .collect(Collectors.toList());
    }

}
