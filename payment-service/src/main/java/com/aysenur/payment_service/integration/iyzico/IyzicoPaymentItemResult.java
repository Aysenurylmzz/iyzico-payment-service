package com.aysenur.payment_service.integration.iyzico;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class IyzicoPaymentItemResult {

    private String paymentTransactionId;
    private String itemId;
    private BigDecimal paidPrice;
    private String transactionStatus;
}
