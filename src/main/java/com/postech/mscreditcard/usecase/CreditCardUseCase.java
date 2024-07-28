package com.postech.mscreditcard.usecase;

import com.postech.mscreditcard.dto.CardDTO;
import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreditCardUseCase {

    public static void validarCartao(CardDTO cardDTO) {
        if (cardDTO == null) {
            throw new IllegalArgumentException("Cartão inválido.");
        }
    }
    public static void validarPagamento(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            throw new IllegalArgumentException("Pagamento inválido.");
        }
    }

}
