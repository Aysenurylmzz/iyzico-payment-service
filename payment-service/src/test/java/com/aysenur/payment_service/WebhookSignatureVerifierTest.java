package com.aysenur.payment_service;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.aysenur.payment_service.config.WebhookSignatureVerifier;
import com.aysenur.payment_service.dto.IyzicoWebhookRequest;

class WebhookSignatureVerifierTest {
     //Gerçek iyzico gizli anahtarı yerine testlerde kullanılacak örnek bir anahtar tanımlanıyor.
    private static final String SECRET_KEY = "test-secret-key";
     //Test edeceğimiz gerçek sınıf oluşturuluyor.
    private final WebhookSignatureVerifier verifier =
            new WebhookSignatureVerifier(SECRET_KEY);

     //Doğru imza gönderildiğinde verify() metodu true dönüyor mu?
    @Test
    void validSignatureShouldReturnTrue() throws Exception {
         //İlk olarak örnek webhook oluşturuluyor.
        IyzicoWebhookRequest request = createRequest();
        //Sonra bu webhook için doğru HMAC imzası hesaplanıyor.
        String signature = calculateSignature(request);

        //Daha sonra doğrulama yapılıyor.
        boolean result = verifier.verify(request, signature);

        assertTrue(result);
    }

    @Test
    void invalidSignatureShouldReturnFalse() {

        IyzicoWebhookRequest request = createRequest();
        //bu sefer gerçek imza yerine "invalid-signature" gönderiliyor.
        boolean result = verifier.verify(
                request,
                "invalid-signature"
        );

        assertFalse(result);
    }

     //Bu test eksik imzayı kontrol ediyor.
    @Test
    void missingSignatureShouldReturnFalse() {

        IyzicoWebhookRequest request = createRequest();

        assertFalse(verifier.verify(request, null));
        assertFalse(verifier.verify(request, ""));
    }

     //Her testte aynı webhook bilgisini tekrar tekrar yazmamak için oluşturulmuş yardımcı metot.
    private IyzicoWebhookRequest createRequest() {

        IyzicoWebhookRequest request =
                new IyzicoWebhookRequest();

        request.setIyziEventType("CHECKOUT_FORM_AUTH");
        request.setIyziPaymentId("payment-123");
        request.setToken("token-123");
        request.setPaymentConversationId("conversation-123");
        request.setStatus("SUCCESS");

        return request;
    }

     //Bu metodun amacı: Webhook için gerçek HMAC imzasını üretmek.
    private String calculateSignature(
            IyzicoWebhookRequest request
    ) throws Exception {
 
        //İlk olarak imzalanacak veri hazırlanıyor.
        String data =
                SECRET_KEY
                        + request.getIyziEventType()
                        + request.getIyziPaymentId()
                        + request.getToken()
                        + request.getPaymentConversationId()
                        + request.getStatus();
 
        //ile HMAC-SHA256 algoritması oluşturuluyor.
        Mac mac = Mac.getInstance("HmacSHA256");

        //Sonra gizli anahtar ekleniyor. Daha sonra gerçek imza hesaplanıyor.
        mac.init(
                new SecretKeySpec(
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                )
        );

        //Son olarak byte dizisi okunabilir hexadecimal formata çevriliyor.
        byte[] hash =
                mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(hash);
    }
}
// bu testte WebhookSignatureVerifier sınıfının farklı seneryolarda da doğru çalıştığı doğrulanıyors