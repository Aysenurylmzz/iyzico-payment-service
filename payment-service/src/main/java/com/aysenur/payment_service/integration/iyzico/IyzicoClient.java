package com.aysenur.payment_service.integration.iyzico;

import java.math.BigDecimal;
import com.iyzipay.model.PaymentItem;
import com.aysenur.payment_service.dto.CheckoutFormRequest;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Currency;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentGroup;
import java.util.ArrayList;
import java.util.List;
import com.iyzipay.Options;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import org.springframework.stereotype.Component;

@Component
public class IyzicoClient {

    private final Options iyzicoOptions;

    public IyzicoClient(Options iyzicoOptions) {
        this.iyzicoOptions = iyzicoOptions;
    }

    public CheckoutFormInitialize initialize(
            CreateCheckoutFormInitializeRequest request
    ) {

        return CheckoutFormInitialize.create(
                request,
                iyzicoOptions
        );
    }

    public CheckoutForm retrieve(
            RetrieveCheckoutFormRequest request
    ) {

        return CheckoutForm.retrieve(
                request,
                iyzicoOptions
        );
    }

    public IyzicoCheckoutInitializeResult initializeCheckoutForm(
        CheckoutFormRequest request,
        String conversationId,
        String callbackUrl
    ) {

    CreateCheckoutFormInitializeRequest iyzicoRequest =
            new CreateCheckoutFormInitializeRequest();

    iyzicoRequest.setLocale(Locale.TR.getValue());
    iyzicoRequest.setConversationId(conversationId);
    iyzicoRequest.setPrice(request.getPrice());
    iyzicoRequest.setPaidPrice(request.getPaidPrice());
    iyzicoRequest.setCurrency(Currency.TRY.name());
    iyzicoRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());
    iyzicoRequest.setCallbackUrl(callbackUrl);

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

    CheckoutFormInitialize response =
        initialize(iyzicoRequest);

    return new IyzicoCheckoutInitializeResult(
           response.getStatus(),
           response.getToken(),
           response.getPaymentPageUrl(),
           response.getErrorMessage()
);
}

    public IyzicoCheckoutResult retrieveCheckoutForm(
        String token,
        String conversationId
    ) {

    RetrieveCheckoutFormRequest request =
            new RetrieveCheckoutFormRequest();

    request.setLocale(Locale.TR.getValue());
    request.setConversationId(conversationId);
    request.setToken(token);

    CheckoutForm response = retrieve(request);

    return new IyzicoCheckoutResult(
          response.getStatus(),
          response.getPaymentId(),
          convertPaymentItems(response.getPaymentItems())
);
}


    public List<IyzicoPaymentItemResult> convertPaymentItems(
        List<PaymentItem> paymentItems
    ) {

    List<IyzicoPaymentItemResult> results = new ArrayList<>();

    for (PaymentItem item : paymentItems) {

        results.add(
                new IyzicoPaymentItemResult(
                        item.getPaymentTransactionId(),
                        item.getItemId(),
                        item.getPaidPrice(),
                        String.valueOf(item.getTransactionStatus())
                )
        );
    }

    return results;
}

}
