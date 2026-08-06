package com.aysenur.payment_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.aysenur.payment_service.dto.CheckoutFormResponse;
import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.entity.Payment;
import com.aysenur.payment_service.entity.PaymentTransaction;
import com.aysenur.payment_service.exception.PaymentNotFoundException;
import com.aysenur.payment_service.integration.iyzico.IyzicoCheckoutInitializeResult;
import com.aysenur.payment_service.integration.iyzico.IyzicoCheckoutResult;
import com.aysenur.payment_service.integration.iyzico.IyzicoClient;
import com.aysenur.payment_service.integration.iyzico.IyzicoPaymentItemResult;
import com.aysenur.payment_service.repository.PaymentRepository;
import com.aysenur.payment_service.repository.PaymentTransactionRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IyzicoClient iyzicoClient;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentService(
        PaymentRepository paymentRepository,
        IyzicoClient iyzicoClient,
        PaymentTransactionRepository paymentTransactionRepository
   ) {
    this.paymentRepository = paymentRepository;
    this.iyzicoClient = iyzicoClient;
    this.paymentTransactionRepository = paymentTransactionRepository;
   }

    public PaymentResponse createPayment(PaymentRequest request) {

    Payment payment = Payment.builder()
            .price(request.getPrice())
            .paidPrice(request.getPaidPrice())
            .currency(request.getCurrency())
            .status("PENDING")
            .build();

    Payment savedPayment = paymentRepository.save(payment);

    return new PaymentResponse(
            savedPayment.getId(),
            savedPayment.getPaymentId(),
            savedPayment.getConversationId(),
            savedPayment.getPrice(),
            savedPayment.getPaidPrice(),
            savedPayment.getCurrency(),
            savedPayment.getStatus(),
            savedPayment.getCreatedAt()
    );
}


    public CheckoutFormResponse initializeCheckoutForm(
        CheckoutFormRequest request) {

    String conversationId =
                UUID.randomUUID().toString();

    IyzicoCheckoutInitializeResult iyzicoResponse =
        iyzicoClient.initializeCheckoutForm(
                request,
                conversationId,
                "https://varnish-slit-defy.ngrok-free.dev/api/payments/callback"
        );

   if ("success".equalsIgnoreCase(iyzicoResponse.getStatus())) {

    Payment payment = Payment.builder()
            .conversationId(conversationId)
            .token(iyzicoResponse.getToken())
            .price(request.getPrice())
            .paidPrice(request.getPaidPrice())
            .currency("TRY")
            .status("PENDING")
            .build();

    paymentRepository.save(payment);
    }

   CheckoutFormResponse response = new CheckoutFormResponse();

   response.setStatus(iyzicoResponse.getStatus());
   response.setPaymentPageUrl(iyzicoResponse.getPaymentPageUrl());
   response.setToken(iyzicoResponse.getToken());
   response.setErrorMessage(iyzicoResponse.getErrorMessage());

   return response;


}
   public String processCallback(String token) {

    Payment payment = paymentRepository.findByToken(token)
            .orElseThrow(() ->
                    new PaymentNotFoundException("Bu token ile ödeme kaydı bulunamadı.")
            );

    if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
        return payment.getConversationId();
    }

    IyzicoCheckoutResult checkoutForm =
        iyzicoClient.retrieveCheckoutForm(
                token,
                payment.getConversationId()
        );

    if ("success".equalsIgnoreCase(checkoutForm.getStatus())) {
       payment.setStatus("SUCCESS");
       payment.setPaymentId(checkoutForm.getPaymentId());
    } else {
         payment.setStatus("FAILURE");
     }

    List<IyzicoPaymentItemResult> paymentItems = checkoutForm.getPaymentItems();

    for (IyzicoPaymentItemResult item : paymentItems) {

    PaymentTransaction transaction = PaymentTransaction.builder()
            .payment(payment)
            .iyzicoTransactionId(item.getPaymentTransactionId())
            .itemId(item.getItemId())
            .paidPrice(item.getPaidPrice())
            .refundedAmount(BigDecimal.ZERO)
            .status(String.valueOf(item.getTransactionStatus()))
            .build();

    paymentTransactionRepository.save(transaction);
  }

    paymentRepository.save(payment);

    return payment.getConversationId();
  }

   public PaymentResponse getPaymentByConversationId(
		String conversationId) {


	Payment payment = paymentRepository
		.findByConversationId(conversationId)
		.orElseThrow(() ->
			new PaymentNotFoundException("Ödeme bulunamadı."));


	return new PaymentResponse(
		payment.getId(),
		payment.getPaymentId(),
                payment.getConversationId(),
                payment.getPrice(),
                payment.getPaidPrice(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
    );
}
}
