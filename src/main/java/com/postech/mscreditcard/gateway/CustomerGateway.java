package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.interfaces.ICustomerGateway;
import com.postech.mscreditcard.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomerGateway implements ICustomerGateway {
    private final CustomerRepository customerRepository;


    public CustomerGateway(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        try {
            //Customer customerNew = new Customer(customerDTO);
            Customer customerNew = null;
            customerNew = customerRepository.save(customerNew);
            return customerNew.toDTO();
        } catch (Exception e) {
            log.error("Error creating customer", e);
            throw new UnknownErrorException("Error creating customer", e);
        }

    }

    @Override
    public CustomerDTO findByCpf(String cpf) {
        try {
            return customerRepository.findByCpf(cpf).toDTO();
        } catch (Exception e) {
            log.error("Error finding customer", e);
            throw new UnknownErrorException("Error finding customer", e);
        }
    }

    public List<CustomerDTO> listAllCustomers() {
        try {
            List<Customer> customerList = customerRepository.findAll();
            return customerList
                    .stream()
                    .map(this::toCustomerDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing customers", e);
            throw new UnknownErrorException("Error listing customers", e);
        }
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        return customer.toDTO();
    }





}
