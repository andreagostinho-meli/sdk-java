package com.mercadopago.example.apis.order;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.order.OrderClient;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.order.Order;

import java.util.HashMap;
import java.util.Map;

public class DeleteOrder {

    public static void main(String[] args) {
        MercadoPagoConfig.setAccessToken("{{ACCESS_TOKEN}}");

        OrderClient client = new OrderClient();

        Map<String, String> headers =  new HashMap<>();
        headers.put("X-Sandbox", "true");
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(headers)
                .build();

        try {
            Order order = client.delete("{{ORDER_ID}}", "{{TRANSACTION_ID}}", requestOptions);
            System.out.println("Deleted order: " + order.getId());
        } catch (MPException | MPApiException e) {
            System.out.println("Error deleting order: " + e.getMessage());
        }

    }

}
