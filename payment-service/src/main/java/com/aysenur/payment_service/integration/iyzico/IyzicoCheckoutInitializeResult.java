package com.aysenur.payment_service.integration.iyzico;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IyzicoCheckoutInitializeResult {

    private String status;
    private String token;
    private String paymentPageUrl;
    private String errorMessage;
}
