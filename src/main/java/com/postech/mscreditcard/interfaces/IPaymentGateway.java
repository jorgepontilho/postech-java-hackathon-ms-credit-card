package com.postech.mscreditcard.interfaces;

import com.postech.mscreditcard.dto.PaymentClientDTO;
import com.postech.mscreditcard.dto.PaymentDTO;
import java.util.List;

public interface IPaymentGateway {

    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentClientDTO findByUuid(String uuid);

    void createPaymentNotOK(PaymentDTO paymentDTO, String status);

    List<PaymentDTO> listAllPayments();
}
