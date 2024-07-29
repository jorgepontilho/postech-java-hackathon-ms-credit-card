package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.PaymentDTO;

public interface IPaymentGateway {

    PaymentDTO createPayment(PaymentDTO paymentDTO);
}
