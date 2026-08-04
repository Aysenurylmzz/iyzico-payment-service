package com.aysenur.payment_service.config;

import com.aysenur.payment_service.dto.IyzicoWebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class WebhookSignatureVerifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secretKey;

    public WebhookSignatureVerifier(
            @Value("${iyzico.secret-key}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    public boolean verify(
            IyzicoWebhookRequest request,
            String receivedSignature
    ) {
        if (receivedSignature == null || receivedSignature.isBlank()) {
            return false;
        }

        String dataToSign =
                secretKey
                        + safe(request.getIyziEventType())
                        + safe(request.getIyziPaymentId())
                        + safe(request.getToken())
                        + safe(request.getPaymentConversationId())
                        + safe(request.getStatus());

        String calculatedSignature = calculateHmac(dataToSign);

        return MessageDigest.isEqual(
                calculatedSignature.getBytes(StandardCharsets.UTF_8),
                receivedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String calculateHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec keySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(keySpec);

            byte[] hash = mac.doFinal(
                    data.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Webhook imzası hesaplanamadı.",
                    exception
            );
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
