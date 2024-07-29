package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.interfaces.IPaymentGateway;
import com.postech.mscreditcard.repository.CardRepository;
import com.postech.mscreditcard.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentGateway implements IPaymentGateway {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;


    public PaymentGateway(PaymentRepository paymentRepository, CardRepository cardRepository) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Card card = cardRepository.findByCardNumber(paymentDTO.getNumero())
                .orElseThrow(()-> new NotFoundException("Cartão não encontrado"));

        Payment paymentNew = new Payment(paymentDTO, card);
        paymentNew = paymentRepository.save(paymentNew);
        return paymentNew.toDTO();
    }

    public Object listAllPayments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
