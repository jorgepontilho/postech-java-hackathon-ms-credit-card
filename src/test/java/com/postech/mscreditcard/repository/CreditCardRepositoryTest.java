package com.postech.mscreditcard.repository;

import com.postech.mscreditcard.entity.CreditCard;
import com.postech.mscreditcard.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CreditCardRepositoryTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = org.mockito.MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void shouldCreateANewCreditCard() {

        CreditCard creditCard = NewEntitiesHelper.newCreditCard();
        int id = creditCard.getId();
        creditCardRepository.save(creditCard);

        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        assertThat(creditCard.getId()).isEqualTo(id);
    }

    @Test
    void shouldFindAllCreditCard() {

        CreditCard creditCard = NewEntitiesHelper.newCreditCard();
        int id = creditCard.getId();
        creditCardRepository.save(creditCard);

        when(creditCardRepository.findAll()).thenReturn(List.of(creditCard));

        List<CreditCard> creditCards = creditCardRepository.findAll();

        assertThat(creditCards).isNotEmpty();
        assertThat(creditCards.get(0)).isEqualTo(creditCard);
    }

    @Test
    void shouldFindAllByCpf() {
        //Arrange
        CreditCard creditCard = NewEntitiesHelper.newCreditCard();
        int id = creditCard.getId();
        creditCardRepository.save(creditCard);

        when(creditCardRepository.findAllByCpf(creditCard.getCpf())).thenReturn(List.of(creditCard));

        //Act
        List<CreditCard> creditCards = creditCardRepository.findAllByCpf(creditCard.getCpf());

        //Assert
        assertThat(creditCards).isNotEmpty();
        assertThat(creditCards.get(0)).isEqualTo(creditCard);
    }
}
