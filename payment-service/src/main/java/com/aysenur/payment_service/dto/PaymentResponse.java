package com.aysenur.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;

    private String paymentId;

    private String conversationId;

    private BigDecimal price;

    private BigDecimal paidPrice;

    private String currency;

    private String status;

    private LocalDateTime createdAt;
}
