package com.aysenur.payment_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IyzicoWebhookRequest {

    private String iyziEventType;
    private String iyziPaymentId;
    private String token;
    private String paymentConversationId;
    private String status;
    private String iyziReferenceCode;
    private Long iyziEventTime;
    private String merchantId;
}
