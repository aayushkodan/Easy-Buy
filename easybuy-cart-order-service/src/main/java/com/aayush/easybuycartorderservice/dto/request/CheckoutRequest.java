package com.aayush.easybuycartorderservice.dto.request;

import com.aayush.easybuycartorderservice.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank String billingName,
        @NotBlank String billingPhone,
        @NotBlank String shippingAddress,
        PaymentMethod paymentMethod,
        String extraInformation,
        String paymentDetails
) {
}