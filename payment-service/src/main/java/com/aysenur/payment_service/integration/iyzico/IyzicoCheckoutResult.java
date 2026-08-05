package com.aysenur.payment_service.integration.iyzico;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class IyzicoCheckoutResult {

    private String status;
    private String paymentId;
    private List<IyzicoPaymentItemResult> paymentItems;
}
