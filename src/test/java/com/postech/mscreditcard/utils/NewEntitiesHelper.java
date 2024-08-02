package com.postech.mscreditcard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.mscreditcard.entity.Card;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NewEntitiesHelper {

    public static final String CARD_NUMBER = "1278 8798 9878 7987";
    public static final String EXPIRATION_DATE = "01/26";
    public static final String CPF = "85838550139";

    public static Customer newCustomer() {
        return new Customer(1L, CPF);
    }

    public static Card newCreditCard() {
        return new Card(1L, CPF, new BigDecimal("1000"), CARD_NUMBER, EXPIRATION_DATE, "111", LocalDateTime.now(), newCustomer());
    }

    public static Payment newPayment() {
        return new Payment(1L, "c5f200ff-6075-4fb7-b5bd-07e930bc9b95"
                , newCustomer(), newCreditCard(), new BigDecimal("100")
                , LocalDateTime.now(), "Compra de produto", "cartao_credito", "aprovado");
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
