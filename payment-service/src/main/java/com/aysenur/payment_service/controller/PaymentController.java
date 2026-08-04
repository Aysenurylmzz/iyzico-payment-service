package com.aysenur.payment_service.controller;

import com.aysenur.payment_service.service.WebhookService;
import com.aysenur.payment_service.dto.IyzicoWebhookRequest;
import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.aysenur.payment_service.dto.CheckoutFormResponse;
import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final WebhookService webhookService;


    public PaymentController(
	   PaymentService paymentService,
	   WebhookService webhookService
    ) {
        this.paymentService = paymentService;
	this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request
    ) {
        PaymentResponse response =
                paymentService.createPayment(request);

        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "http://localhost:5173")
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
        String conversationId =
                paymentService.processCallback(token);

        return ResponseEntity.status(302)
		.header(
			"Location",
			"http://localhost:5173/payment/result?ref="
				+ conversationId
     		)
		.build();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{conversationId}")
    public ResponseEntity<PaymentResponse> getPaymentByConversationId(
            @PathVariable String conversationId
    ) {
        PaymentResponse response =
                paymentService.getPaymentByConversationId(conversationId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
          @RequestBody IyzicoWebhookRequest request,
          @RequestHeader("X-IYZ-SIGNATURE-V3") String signature
    ) {

       boolean success =
              webhookService.processWebhook(request, signature);

       if (!success) {
            return ResponseEntity
                  .status(HttpStatus.UNAUTHORIZED)
                  .body("Geçersiz webhook imzası.");
       }

        return ResponseEntity.ok("Webhook başarıyla işlendi.");
}
}
