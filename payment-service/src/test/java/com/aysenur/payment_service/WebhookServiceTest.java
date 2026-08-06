package com.aysenur.payment_service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aysenur.payment_service.config.WebhookSignatureVerifier;
import com.aysenur.payment_service.dto.IyzicoWebhookRequest;
import com.aysenur.payment_service.repository.WebhookEventRepository;
import com.aysenur.payment_service.service.PaymentService;
import com.aysenur.payment_service.service.WebhookService;

import tools.jackson.databind.json.JsonMapper;

class WebhookServiceTest {

    @Test
    void sameWebhookShouldNotBeProcessedTwice() {
        // gerçek veritabanına bağlanmak yerine sahte repository oluşturuyor
        WebhookEventRepository repository =
                mock(WebhookEventRepository.class);
        // gerçek imza hesaplaması yapılmasın diye sahte doğrulayıcı oluşturuyor
        WebhookSignatureVerifier signatureVerifier =
                mock(WebhookSignatureVerifier.class);
        // gerçek ödeme işlemi çalışmasın diye sahte servis oluşturuyor
        PaymentService paymentService =
                mock(PaymentService.class);
        // json için sahte nesne oluşturuyor
        JsonMapper jsonMapper =
                mock(JsonMapper.class);
        //burada sahte bağımlılıklarla gerçek webhookservice oluşturuluyor
        WebhookService webhookService =
                new WebhookService(
                        repository,
                        signatureVerifier,
                        paymentService,
                        jsonMapper
                );
        //reference-123, webhook’un benzersiz referansı gibi düşünülüyor.
        IyzicoWebhookRequest request =
                new IyzicoWebhookRequest();

        request.setIyziReferenceCode("reference-123");
        request.setToken("token-123");
        //Bu istek ve imza geldiğinde, imza doğru kabul edilsin.
        when(signatureVerifier.verify(request, "valid-signature"))
                .thenReturn(true);

        // bu webhook daha önce veritabanına kaydedilmiş kabul edilsin.
        when(repository.existsByIyzicoRef("reference-123"))
                .thenReturn(true);

        boolean result = webhookService.processWebhook(
                request,
                "valid-signature"
        );

        assertTrue(result);

        //Aynı webhook ikinci kez geldiği için callback tekrar çağrılmamalı.
        verify(paymentService, never())
                .processCallback(anyString());
        //Yeni webhook kaydı da oluşturulmamalı.
        verify(repository, never())
                .save(any());
    }
}
//bu test daha önce işlenmiş aynı webhook tekrar geldiğinde sistemin ödeme işlemini ikinci kez çalıştırmadığını ve yeni kayıt oluşturmadığını doğrular.