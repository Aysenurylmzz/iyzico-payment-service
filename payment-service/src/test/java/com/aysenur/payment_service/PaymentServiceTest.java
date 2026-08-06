package com.aysenur.payment_service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aysenur.payment_service.entity.Payment;
import com.aysenur.payment_service.integration.iyzico.IyzicoClient;
import com.aysenur.payment_service.repository.PaymentRepository;
import com.aysenur.payment_service.repository.PaymentTransactionRepository;
import com.aysenur.payment_service.service.PaymentService;

class PaymentServiceTest {

    @Test // başarılı olmuş bir ödeme tekrar işlenmiyor
    void successfulPaymentShouldNotBeProcessedAgain() {

        PaymentRepository paymentRepository =
                mock(PaymentRepository.class);

        IyzicoClient iyzicoClient =
                mock(IyzicoClient.class);

        PaymentTransactionRepository transactionRepository =
                mock(PaymentTransactionRepository.class);

        //burada artık test edeceğimiz gerçek sınıf oluşturuluyor
        PaymentService paymentService =
                new PaymentService(
                        paymentRepository,
                        iyzicoClient,
                        transactionRepository
                );

        // örnek bir payment oluşturuyoruz yani burada sistemde böyle bir ödeme var diyoruz
        Payment payment = Payment.builder()
                .conversationId("conversation-123")
                .token("token-123")
                .status("SUCCESS")
                .build();

        //findbByToken çağırılırsa payment döndürülüyor
        when(paymentRepository.findByToken("token-123"))
                .thenReturn(Optional.of(payment));

        //paymentservice içine giriyor orada ilk yaptığı şey findToken("token-123") çağırmak
        String result =
                paymentService.processCallback("token-123");

        assertEquals("conversation-123", result);

        // retrieveCheckoutForm() metodu hiç çağırılmamamlı çünkü ödeme zaten basarılı
        verify(iyzicoClient, never())
                .retrieveCheckoutForm(anyString(), anyString());

        //veritabanına tekrar kayıt yapılmamlı
        verify(paymentRepository, never())
                .save(any());
    }
}
