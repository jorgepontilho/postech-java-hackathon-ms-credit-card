package com.postech.mscreditcard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.mscreditcard.entity.CreditCard;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.entity.Payment;

import java.math.BigDecimal;

public class NewEntitiesHelper {

    public static Customer newCustomer() {
        return new Customer(1, "194693002-19");
    }

    public static CreditCard newCreditCard() {
        return new CreditCard(1, "1278-8798-9878-7987", "194693002-19", "111", "01/26", BigDecimal.valueOf(100.00), new Customer(2, "124693002-11"));
    }

    public static Payment newPayment() {
        return new Payment(1, "194693002-19", "1278-8798-9878-7987", "01/26", "111", 100.00);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
