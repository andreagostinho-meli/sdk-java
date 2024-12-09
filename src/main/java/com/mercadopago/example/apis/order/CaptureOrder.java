package com.mercadopago.example.apis.order;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.order.*;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.net.Headers;
import com.mercadopago.resources.order.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureOrder {

    public static void main(String[] args) {
        MercadoPagoConfig.setAccessToken("{{ACCESS_TOKEN}}");

        OrderClient client = new OrderClient();

        OrderPaymentRequest payment = OrderPaymentRequest.builder()
                .amount("10.00")
                .paymentMethod(OrderPaymentMethodRequest.builder()
                        .id("visa")
                        .type("credit_card")
                        .token("{{CARD_TOKEN}}")
                        .installments(1)
                        .build())
                .build();

        List<OrderPaymentRequest> payments = new ArrayList<>();
        payments.add(payment);

        OrderTypeConfigRequest typeConfig = OrderTypeConfigRequest.builder()
                .captureMode("manual")
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .type("online")
                .processingMode("automatic")
                .totalAmount("10.00")
                .typeConfig(typeConfig)
                .externalReference("ext_ref")
                .payer(OrderPayerRequest.builder().email("test@test.com").build())
                .transactions(OrderTransactionRequest.builder()
                        .payments(payments)
                        .build())
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put(Headers.SANDBOX, "true");
        headers.put(Headers.IDEMPOTENCY_KEY, "{{UNIQUE_NUMBER_CREATE}}");
        headers.put(Headers.SITE_ID, "MLB");
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(headers)
                .build();

        // RequestOptions to capture the order's flow
        Map<String, String> headersCapture = new HashMap<>();
        headers.put(Headers.SANDBOX, "true");
        headers.put(Headers.IDEMPOTENCY_KEY, "{{UNIQUE_NUMBER_CAPTURE}}");
        headers.put(Headers.SITE_ID, "MLB");
        MPRequestOptions requestOptionsCapture = MPRequestOptions.builder()
                .customHeaders(headersCapture)
                .build();

        try {
            Order order = client.create(request, requestOptions);
            System.out.println("Order created: " + order.getId());
            Order capturedOrder = client.capture(order.getId(), requestOptionsCapture);
            System.out.println("Captured order: " + capturedOrder.getId());
        } catch (Exception e) {
            System.out.println("Error creating order: " + e.getMessage());
        }

    }

}
