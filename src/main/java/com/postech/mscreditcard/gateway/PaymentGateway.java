package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.PaymentDTO;
import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Payment;
import com.postech.mscreditcard.exceptions.NotFoundException;
import com.postech.mscreditcard.exceptions.UnknownErrorException;
import com.postech.mscreditcard.interfaces.IPaymentGateway;
import com.postech.mscreditcard.repository.CardRepository;
import com.postech.mscreditcard.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PaymentDTO findByUuid(String uuid) {
        try {
            return paymentRepository.findByUuid(uuid).orElseThrow(() -> {
                throw new NotFoundException("Pagamento não encontrado");
            }).toDTO();
        } catch (NotFoundException ne) {
            throw ne;
        } catch (Exception e) {
            log.error("Error finding customer", e);
            throw new UnknownErrorException("Error finding customer", e);
        }
    }
}
