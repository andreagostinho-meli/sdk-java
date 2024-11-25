package com.mercadopago.client.order;

import com.mercadopago.client.cardtoken.CardTokenTestClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.CardToken;
import com.mercadopago.resources.order.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.net.HttpStatus.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderClientIT {
  private final OrderClient client = new OrderClient();

  private final CardTokenTestClient cardTokenTestClient = new CardTokenTestClient();

  @Test
  public void createSuccess() {
    try {
      CardToken cardToken = cardTokenTestClient.createTestCardToken("approved");


      List<OrderPaymentRequest> paymentRequest = new ArrayList<OrderPaymentRequest>();
      paymentRequest.add(
          OrderPaymentRequest.builder()
              .amount("10.00")
              .currency("BRL")
              .paymentMethod(
                  OrderPaymentMethodRequest.builder()
                      .id("master")
                      .type("credit_card")
                      .token(cardToken.getId())
                      .installments(1)
                  .build())
          .build());

      OrderCreateRequest orderCreateRequest =
          OrderCreateRequest.builder()
              .totalAmount("10.00")
              .description("test")
              .transactions(OrderTransactionRequest.builder()
                  .payments(paymentRequest)
                  .build())
              .build();

      Order order = client.create(orderCreateRequest);

      assertNotNull(order.getResponse());
      assertEquals(CREATED, order.getResponse().getStatusCode());
      assertNotNull(order.getId());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }

}
