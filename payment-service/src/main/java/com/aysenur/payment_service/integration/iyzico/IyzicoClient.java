package com.aysenur.payment_service.integration.iyzico;

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
}
