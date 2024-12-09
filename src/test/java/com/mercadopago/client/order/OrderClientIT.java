package com.mercadopago.client.order;

import com.mercadopago.BaseClientIT;
import com.mercadopago.client.cardtoken.CardTokenTestClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.CardToken;
import com.mercadopago.resources.order.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.net.HttpStatus.CREATED;
import static com.mercadopago.net.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/** OrderClientIT class. */
public class OrderClientIT extends BaseClientIT {
  private final OrderClient client = new OrderClient();
  private final CardTokenTestClient cardTokenTestClient = new CardTokenTestClient();

  @Test
  public void createOrderSuccess() {
    try {
      CardToken cardToken = cardTokenTestClient.createTestCardToken("approved");
      List<OrderPaymentRequest> paymentRequest = new ArrayList<>();
      paymentRequest.add(OrderPaymentRequest.builder()
              .amount("100.00")
              .paymentMethod(OrderPaymentMethodRequest.builder()
                      .id("master")
                      .type("credit_card")
                      .token(cardToken.getId())
                      .installments(1)
                      .build())
              .build());
      OrderPayerRequest orderPayerRequest = OrderPayerRequest.builder()
              .email("test_1731350184@testuser.com")
              .build();
      OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
              .type("online")
              .totalAmount("100.00")
              .processingMode("automatic")
              .externalReference("ext_ref_1234")
              .payer(orderPayerRequest)
              .transactions(OrderTransactionRequest.builder()
                      .payments(paymentRequest)
                      .build())
              .build();

      Order order = client.create(orderCreateRequest);

      assertNotNull(order.getResponse());
      assertEquals(CREATED, order.getResponse().getStatusCode());
      assertNotNull(order.getId());
      assertEquals("100.00", order.getTotalAmount());
      assertEquals("master", order.getTransactions().getPayments().get(0).getPaymentMethod().getId());
      assertEquals("credit_card", order.getTransactions().getPayments().get(0).getPaymentMethod().getType());
      assertEquals(1, order.getTransactions().getPayments().get(0).getPaymentMethod().getInstallments());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }

  @Test
  public void getOrderSuccess() {
    try {
      List<OrderPaymentRequest> paymentRequest = new ArrayList<>();
      paymentRequest.add(OrderPaymentRequest.builder()
              .amount("100.00")
              .paymentMethod(OrderPaymentMethodRequest.builder()
                      .id("pix")
                      .type("bank_transfer")
                      .build())
              .build());
      OrderPayerRequest orderPayerRequest = OrderPayerRequest.builder()
              .email("test_1731350184@testuser.com")
              .build();
      OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
              .type("online")
              .totalAmount("100.00")
              .processingMode("automatic")
              .externalReference("ext_ref_1234")
              .payer(orderPayerRequest)
              .transactions(OrderTransactionRequest.builder()
                      .payments(paymentRequest)
                      .build())
              .build();

      Order order = client.create(orderCreateRequest);
      Order foundOrder = client.get(order.getId());

      assertNotNull(foundOrder.getResponse());
      assertEquals(OK, foundOrder.getResponse().getStatusCode());
      assertEquals(order.getId(), foundOrder.getId());
      assertEquals("100.00", foundOrder.getTotalAmount());
      assertEquals("pix", foundOrder.getTransactions().getPayments().get(0).getPaymentMethod().getId());
      assertEquals("bank_transfer", foundOrder.getTransactions().getPayments().get(0).getPaymentMethod().getType());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }

  @Test
  public void processOrderSuccess() {
    try {
      CardToken cardToken = cardTokenTestClient.createTestCardToken("approved");
      List<OrderPaymentRequest> paymentRequest = new ArrayList<>();
      paymentRequest.add(OrderPaymentRequest.builder()
              .amount("100.00")
              .paymentMethod(OrderPaymentMethodRequest.builder()
                      .id("master")
                      .type("credit_card")
                      .token(cardToken.getId())
                      .installments(1)
                      .build())
              .build());
      OrderPayerRequest orderPayerRequest = OrderPayerRequest.builder()
              .email("test_1731350184@testuser.com")
              .build();
      OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
              .type("online")
              .totalAmount("100.00")
              .processingMode("manual")
              .externalReference("ext_ref_1234")
              .payer(orderPayerRequest)
              .transactions(OrderTransactionRequest.builder()
                      .payments(paymentRequest)
                      .build())
              .build();

      Order order = client.create(orderCreateRequest);
      Order processedOrder = client.process(order.getId());

      assertNotNull(processedOrder.getResponse());
      assertEquals(OK, processedOrder.getResponse().getStatusCode());
      assertNotNull(order.getId());
      assertEquals("100.00", processedOrder.getTotalAmount());
      assertEquals("processed", processedOrder.getStatus());
      assertEquals("processed", processedOrder.getTransactions().getPayments().get(0).getStatus());
      assertEquals("accredited", processedOrder.getTransactions().getPayments().get(0).getStatusDetail());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }

  @Test
  public void captureOrderSuccess() {
    try {
      CardToken cardToken = cardTokenTestClient.createTestCardToken("approved");
      List<OrderPaymentRequest> paymentRequest = new ArrayList<>();
      paymentRequest.add(OrderPaymentRequest.builder()
              .amount("100.00")
              .paymentMethod(OrderPaymentMethodRequest.builder()
                      .id("master")
                      .type("credit_card")
                      .token(cardToken.getId())
                      .installments(1)
                      .build())
              .build());
      OrderPayerRequest orderPayerRequest = OrderPayerRequest.builder()
              .email("test_1731350184@testuser.com")
              .build();
      OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
              .type("online")
              .totalAmount("100.00")
              .processingMode("automatic")
              .captureMode("manual")
              .externalReference("ext_ref_1234")
              .payer(orderPayerRequest)
              .transactions(OrderTransactionRequest.builder()
                      .payments(paymentRequest)
                      .build())
              .build();

      Order order = client.create(orderCreateRequest);
      Order capturedOrder = client.capture(order.getId());

      assertNotNull(capturedOrder.getResponse());
      assertEquals(OK, capturedOrder.getResponse().getStatusCode());
      assertEquals(order.getId(), capturedOrder.getId());
      assertEquals("processed", capturedOrder.getStatus());
      assertEquals("100.00", capturedOrder.getTransactions().getPayments().get(0).getAmount());
      assertEquals("processed", capturedOrder.getTransactions().getPayments().get(0).getStatus());
      assertEquals("accredited", capturedOrder.getTransactions().getPayments().get(0).getStatusDetail());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }

  @Test
  public void cancelOrderSuccess() {
    try {
      CardToken cardToken = cardTokenTestClient.createTestCardToken("approved");
      List<OrderPaymentRequest> paymentRequest = new ArrayList<>();
      paymentRequest.add(OrderPaymentRequest.builder()
              .amount("100.00")
              .paymentMethod(OrderPaymentMethodRequest.builder()
                      .id("master")
                      .type("credit_card")
                      .token(cardToken.getId())
                      .installments(1)
                      .build())
              .build());
      OrderPayerRequest orderPayerRequest = OrderPayerRequest.builder()
              .email("test_1731350184@testuser.com")
              .build();
      OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
              .type("online")
              .totalAmount("100.00")
              .processingMode("manual")
              .externalReference("ext_ref_1234")
              .payer(orderPayerRequest)
              .transactions(OrderTransactionRequest.builder()
                      .payments(paymentRequest)
                      .build())
              .build();

      Order order = client.create(orderCreateRequest);
      Order cancelledOrder = client.cancel(order.getId());

      assertNotNull(cancelledOrder.getResponse());
      assertEquals(OK, cancelledOrder.getResponse().getStatusCode());
      assertEquals(order.getId(), cancelledOrder.getId());
      assertEquals("cancelled", cancelledOrder.getStatus());
      assertEquals("cancelled", cancelledOrder.getTransactions().getPayments().get(0).getStatus());
      assertEquals("cancelled_transaction", cancelledOrder.getTransactions().getPayments().get(0).getStatusDetail());
    } catch (MPApiException mpApiException) {
      fail(mpApiException.getApiResponse().getContent());
    } catch (MPException mpException) {
      fail(mpException.getMessage());
    }
  }
}
