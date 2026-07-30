package com.aysenur.payment_service.service;

import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import java.util.ArrayList;
import java.util.List;
import com.iyzipay.model.Address;
import com.iyzipay.model.Buyer;
import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.aysenur.payment_service.dto.CheckoutFormResponse;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.Options;
import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.entity.Payment;
import com.aysenur.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Options iyzicoOptions;

    public PaymentService(PaymentRepository paymentRepository,
			 Options iyzicoOptions) {

	this.paymentRepository = paymentRepository;
	this.iyzicoOptions = iyzicoOptions;
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
    iyzicoRequest.setConversationId(
            "conversation-" + System.currentTimeMillis()
    );

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
        CheckoutFormInitialize.create(
                iyzicoRequest,
                iyzicoOptions
        );
 
   CheckoutFormResponse response = new CheckoutFormResponse();

   response.setStatus(iyzicoResponse.getStatus());
   response.setPaymentPageUrl(iyzicoResponse.getPaymentPageUrl());
   response.setToken(iyzicoResponse.getToken());
   response.setErrorMessage(iyzicoResponse.getErrorMessage());

   return response;


}
}
