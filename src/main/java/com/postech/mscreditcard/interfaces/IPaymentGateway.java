package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.PaymentDTO;
import java.util.List;

public interface IPaymentGateway {
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    List<PaymentDTO> listAllPayments();
}
