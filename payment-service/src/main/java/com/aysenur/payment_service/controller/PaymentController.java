package com.aysenur.payment_service.controller;

import org.springframework.web.bind.annotation.RequestParam;
import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.aysenur.payment_service.dto.CheckoutFormResponse;
import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout-form")
    public ResponseEntity<CheckoutFormResponse> initializeCheckoutForm(
            @RequestBody CheckoutFormRequest request
    ) {

        CheckoutFormResponse response =
               paymentService.initializeCheckoutForm(request);

        return ResponseEntity.ok(response);
   }

    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(
           @RequestParam String token
    ) {

          return ResponseEntity.ok(
                 "Ödeme callback alındı. Token: " + token
      );
}

}
