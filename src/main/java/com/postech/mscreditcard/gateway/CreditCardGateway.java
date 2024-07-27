package com.postech.mscreditcard.gateway;

import com.postech.mscreditcard.dto.*;
import com.postech.mscreditcard.entity.*;
import com.postech.mscreditcard.interfaces.ICreditCardGateway;
import com.postech.mscreditcard.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreditCardGateway implements ICreditCardGateway {
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public UserDTO findByLoginAndPassword(String login, String password) {
        try {
            //Login e usuário definido pelos professores
            if ("adj2" .equals(login) && "adj@1234" .equals(password)) {
                return new UserDTO(login, login, password);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public CreditCardGateway(CustomerRepository customerRepository, CardRepository cardRepository, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customerNew = new Customer(customerDTO);
        customerNew = customerRepository.save(customerNew);
        return customerNew.toDTO();
    }

    @Override
    public CustomerDTO findByCpf(String cpf) {
        return customerRepository.findByCpf(cpf).toDTO();
    }

    public CardDTO createCard(CardDTO cardDTO) {
        Card CardNew = new Card(cardDTO);
        CardNew = cardRepository.save(CardNew);
        return CardNew.toDTO();
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment paymentNew = new Payment(paymentDTO);
        paymentNew = paymentRepository.save(paymentNew);
        return paymentNew.toDTO();
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        return customer.toDTO();
    }

    private CardDTO toCardDTO(Card card) {
        return card.toDTO();
    }

    private PaymentDTO toPaymentDTO(Payment payment) {
        return payment.toDTO();
    }

    public List<CustomerDTO> listAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        return customerList
                .stream()
                .map(this::toCustomerDTO)
                .collect(Collectors.toList());
    }

    public List<CardDTO> listAllCards() {
        List<Card> cardList = cardRepository.findAll();
        return cardList
                .stream()
                .map(this::toCardDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> listAllPayments() {
        List<Payment> paymentList = paymentRepository.findAll();
        return paymentList
                .stream()
                .map(this::toPaymentDTO)
                .collect(Collectors.toList());
    }

}
