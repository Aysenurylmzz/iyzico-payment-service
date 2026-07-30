package com.aysenur.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutFormRequest {

    private BigDecimal price;
    private BigDecimal paidPrice;
    private String currency;


    private String buyerName;
    private String buyerSurname;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerIdentityNumber;

    private String address;
    private String city;
    private String country;
    private String zipCode;
}
