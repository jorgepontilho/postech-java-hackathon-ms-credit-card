package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.PaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentUseCase {

    public static void validateCardCreationPayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            throw new IllegalArgumentException("Pagamento inválido.");
        }
    }

}
