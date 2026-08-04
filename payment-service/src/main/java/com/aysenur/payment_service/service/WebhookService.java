package com.aysenur.payment_service.service;

import com.aysenur.payment_service.config.WebhookSignatureVerifier;
import com.aysenur.payment_service.dto.IyzicoWebhookRequest;
import com.aysenur.payment_service.entity.WebhookEvent;
import com.aysenur.payment_service.repository.WebhookEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebhookService {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookSignatureVerifier signatureVerifier;
    private final PaymentService paymentService;
    private final JsonMapper jsonMapper;

    public WebhookService(
            WebhookEventRepository webhookEventRepository,
            WebhookSignatureVerifier signatureVerifier,
            PaymentService paymentService,
            JsonMapper jsonMapper
    ) {
        this.webhookEventRepository = webhookEventRepository;
        this.signatureVerifier = signatureVerifier;
        this.paymentService = paymentService;
        this.jsonMapper = jsonMapper;
    }

    public boolean processWebhook(
            IyzicoWebhookRequest request,
            String receivedSignature
    ) {

        boolean signatureValid =
                signatureVerifier.verify(request, receivedSignature);

        if (!signatureValid) {
            saveWebhookEvent(request, false, null);
            return false;
        }

        if (webhookEventRepository.existsByIyzicoRef(
                request.getIyziReferenceCode()
        )) {
            return true;
        }

        WebhookEvent webhookEvent =
                saveWebhookEvent(request, true, null);

        paymentService.processCallback(request.getToken());

        webhookEvent.setProcessedAt(LocalDateTime.now());
        webhookEventRepository.save(webhookEvent);

        return true;
    }

    private WebhookEvent saveWebhookEvent(
            IyzicoWebhookRequest request,
            boolean signatureOk,
            LocalDateTime processedAt
    ) {

        WebhookEvent webhookEvent = WebhookEvent.builder()
                .eventType(request.getIyziEventType())
                .iyzicoRef(request.getIyziReferenceCode())
                .payload(toJson(request))
                .signatureOk(signatureOk)
                .processedAt(processedAt)
                .receivedAt(LocalDateTime.now())
                .build();

        return webhookEventRepository.save(webhookEvent);
    }

    private String toJson(IyzicoWebhookRequest request) {
        try {
            return jsonMapper.writeValueAsString(request);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Webhook verisi JSON formatına dönüştürülemedi.",
                    exception
            );
        }
    }
}
