package com.aysenur.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutFormResponse {

    private String status;

    private String paymentPageUrl;

    private String token;

    private String errorMessage;
}
