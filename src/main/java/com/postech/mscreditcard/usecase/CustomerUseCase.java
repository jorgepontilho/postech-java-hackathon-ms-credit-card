package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.gateway.CustomerGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerUseCase {

    private final CustomerGateway customerGateway;

    public CustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public boolean canCreateCustomer(CustomerDTO customerDTO) {
        boolean canCreateCustomer = false;
        try {
            if (customerDTO == null) {
                throw new IllegalArgumentException("Cliente inválido.");
            }
            canCreateCustomer = customerGateway.findByCpf(customerDTO.getCpf()) == null;
        } catch (NotFoundException nf) {
            canCreateCustomer = true;
        } catch (UnknownErrorException ue) {
            throw ue;
        }
        return canCreateCustomer;
    }
}
