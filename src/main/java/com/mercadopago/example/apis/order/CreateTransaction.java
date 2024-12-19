package com.mercadopago.example.apis.order;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.order.OrderClient;
import com.mercadopago.client.order.OrderPaymentMethodRequest;
import com.mercadopago.client.order.OrderPaymentRequest;
import com.mercadopago.client.order.OrderTransactionRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.net.MPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mercado Pago Create Order transaction.
 *
 * @see <a href="https://mercadopago.com/developers/en/reference/order/online-payments/add-transaction/post">Documentation</a>
 */
public class CreateTransaction{

    public static void main(String[] args) {
        MercadoPagoConfig.setAccessToken("{{ACCESS_TOKEN}}");
        String orderId = "{{ORDER_ID}}";

        OrderClient client = new OrderClient();

        OrderPaymentMethodRequest paymentMethodRequest = OrderPaymentMethodRequest.builder()
                .id("master")
                .type("credit_card")
                .token("{{CARD_TOKEN}}")
                .installments(1)
                .statementDescriptor("statement")
                .build();

        OrderPaymentRequest paymentRequest = OrderPaymentRequest.builder()
                .amount("100.00")
                .paymentMethod(paymentMethodRequest)
                .build();

        List<OrderPaymentRequest> payments = new ArrayList<>();
        payments.add(paymentRequest);

        OrderTransactionRequest transactionRequest = OrderTransactionRequest.builder()
                .payments(payments)
                .build();

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Idempotency-Key", "{{IDEMPOTENCY_KEY}}");

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(headers)
                .build();

        try{
            MPResponse response = client.createTransaction(orderId, transactionRequest, requestOptions).getResponse();
            System.out.println("Order transaction created: " + response.getContent());
        } catch (Exception e) {
            System.out.println("Error creating order transaction: " + e.getMessage());
        }
    }
}