package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerUseCase {
    public static void validarCliente(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            throw new IllegalArgumentException("Cliente inválido.");
        }
    }

}
