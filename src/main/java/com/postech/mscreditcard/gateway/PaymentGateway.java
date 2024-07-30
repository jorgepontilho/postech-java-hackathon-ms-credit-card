package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.interfaces.IPaymentGateway;
import com.postech.mscreditcard.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentGateway implements IPaymentGateway {
    private final PaymentRepository paymentRepository;

    public PaymentGateway(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment paymentNew = new Payment(paymentDTO);
        paymentNew = paymentRepository.save(paymentNew);
        return paymentNew.toDTO();
    }

    private PaymentDTO toPaymentDTO(Payment payment) {
        return payment.toDTO();
    }

    public List<PaymentDTO> listAllPayments() {
        List<Payment> paymentList = paymentRepository.findAll();
        return paymentList
                .stream()
                .map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

}
