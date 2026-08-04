package com.aysenur.payment_service.service;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.iyzipay.model.PaymentItem;
import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.aysenur.payment_service.dto.CheckoutFormResponse;
import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.entity.Payment;
import com.aysenur.payment_service.entity.PaymentTransaction;
import com.aysenur.payment_service.integration.iyzico.IyzicoClient;
import com.aysenur.payment_service.repository.PaymentRepository;
import com.aysenur.payment_service.repository.PaymentTransactionRepository;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;

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

    CreateCheckoutFormInitializeRequest iyzicoRequest =
            new CreateCheckoutFormInitializeRequest();

    iyzicoRequest.setLocale(Locale.TR.getValue());
    String conversationId =
        "conversation-" + System.currentTimeMillis();

    iyzicoRequest.setConversationId(conversationId);

    iyzicoRequest.setPrice(request.getPrice());
    iyzicoRequest.setPaidPrice(request.getPaidPrice());
    iyzicoRequest.setCurrency(Currency.TRY.name());
    iyzicoRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());

    iyzicoRequest.setCallbackUrl(
        "http://localhost:8080/api/payments/callback"
    );

   Buyer buyer = new Buyer();

   buyer.setId("buyer-" + System.currentTimeMillis());
   buyer.setName(request.getBuyerName());
   buyer.setSurname(request.getBuyerSurname());
   buyer.setEmail(request.getBuyerEmail());
   buyer.setGsmNumber(request.getBuyerPhone());
   buyer.setIdentityNumber(request.getBuyerIdentityNumber());

   buyer.setRegistrationAddress(request.getAddress());
   buyer.setCity(request.getCity());
   buyer.setCountry(request.getCountry());
   buyer.setZipCode(request.getZipCode());

   buyer.setIp("127.0.0.1");

   iyzicoRequest.setBuyer(buyer);

   Address shippingAddress = new Address();

   shippingAddress.setContactName(
          request.getBuyerName() + " " + request.getBuyerSurname()
   );

   shippingAddress.setAddress(request.getAddress());
   shippingAddress.setCity(request.getCity());
   shippingAddress.setCountry(request.getCountry());
   shippingAddress.setZipCode(request.getZipCode());

   iyzicoRequest.setShippingAddress(shippingAddress);


   Address billingAddress = new Address();

   billingAddress.setContactName(
          request.getBuyerName() + " " + request.getBuyerSurname()
   );

   billingAddress.setAddress(request.getAddress());
   billingAddress.setCity(request.getCity());
   billingAddress.setCountry(request.getCountry());
   billingAddress.setZipCode(request.getZipCode());

   iyzicoRequest.setBillingAddress(billingAddress);

   List<BasketItem> basketItems = new ArrayList<>();


   BasketItem basketItem = new BasketItem();

   basketItem.setId("item-1");
   basketItem.setName("Test Ürünü");
   basketItem.setCategory1("Genel");
   basketItem.setItemType(BasketItemType.VIRTUAL.name());
   basketItem.setPrice(request.getPrice());

   basketItems.add(basketItem);

   iyzicoRequest.setBasketItems(basketItems);

   CheckoutFormInitialize iyzicoResponse =
        iyzicoClient.initialize(iyzicoRequest);

   if ("success".equalsIgnoreCase(iyzicoResponse.getStatus())) {

    Payment payment = Payment.builder()
            .conversationId(conversationId)
            .token(iyzicoResponse.getToken())
            .price(request.getPrice())
            .paidPrice(request.getPaidPrice())
            .currency(Currency.TRY.name())
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
                    new RuntimeException("Bu token ile ödeme kaydı bulunamadı.")
            );

    RetrieveCheckoutFormRequest retrieveRequest =
            new RetrieveCheckoutFormRequest();

    retrieveRequest.setLocale(Locale.TR.getValue());
    retrieveRequest.setConversationId(payment.getConversationId());
    retrieveRequest.setToken(token);

    CheckoutForm checkoutForm =
            iyzicoClient.retrieve(retrieveRequest);

    if ("success".equalsIgnoreCase(checkoutForm.getStatus())) {
        payment.setStatus("SUCCESS");
        payment.setPaymentId(checkoutForm.getPaymentId());
    } else {
        payment.setStatus("FAILURE");
    }

    List<PaymentItem> paymentItems = checkoutForm.getPaymentItems();

    for (PaymentItem item : paymentItems) {

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
			new RuntimeException("Ödeme bulunamadı."));


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
