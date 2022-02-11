package com.mercadopago.client.paymentmethod;

import static com.mercadopago.helper.MockHelper.generateHttpResponseFromFile;
import static com.mercadopago.net.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import com.mercadopago.BaseClientTest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResourceList;
import com.mercadopago.resources.paymentmethod.PaymentMethod;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;

class PaymentMethodClientTest extends BaseClientTest {

  private static final String PAYMENT_METHOD_BASE_JSON = "paymentmethod/payment_method_base.json";

  private static final int DEFAULT_TIMEOUT = 1000;

  private static final String THUMBNAIL =
      "https://www.mercadopago.com/org-img/MP3/API/logos/debmaster.gif";

  private static final Long ACCREDITATION_TIME = 1440L;

  private final PaymentMethodClient client = new PaymentMethodClient();

  @Test
  void listSuccess() throws MPException, MPApiException, IOException {
    HttpResponse httpResponse = generateHttpResponseFromFile(PAYMENT_METHOD_BASE_JSON, OK);
    doReturn(httpResponse)
        .when(httpClient)
        .execute(any(HttpRequestBase.class), any(HttpContext.class));

    MPResourceList<PaymentMethod> paymentMethods = client.list();
    assertNotNull(paymentMethods.getResponse());
    assertEquals(OK, paymentMethods.getResponse().getStatusCode());
    assertPaymentMethodFields(paymentMethods);
  }

  @Test
  public void listSuccessWithRequestOptions() throws IOException, MPException, MPApiException {
    MPRequestOptions requestOptions =
        MPRequestOptions.builder()
            .accessToken("abc")
            .connectionTimeout(DEFAULT_TIMEOUT)
            .connectionRequestTimeout(DEFAULT_TIMEOUT)
            .socketTimeout(DEFAULT_TIMEOUT)
            .build();
    HttpResponse httpResponse = generateHttpResponseFromFile(PAYMENT_METHOD_BASE_JSON, OK);
    doReturn(httpResponse)
        .when(httpClient)
        .execute(any(HttpRequestBase.class), any(HttpContext.class));

    MPResourceList<PaymentMethod> paymentMethods = client.list(requestOptions);
    assertNotNull(paymentMethods.getResponse());
    assertEquals(OK, paymentMethods.getResponse().getStatusCode());
    assertPaymentMethodFields(paymentMethods);
  }

  private void assertPaymentMethodFields(MPResourceList<PaymentMethod> paymentMethods) {
    List<String> additionalInfoNeeded = new ArrayList<>();
    additionalInfoNeeded.add("cardholder_name");
    additionalInfoNeeded.add("cardholder_identification_type");
    additionalInfoNeeded.add("cardholder_identification_number");

    assertEquals("debmaster", paymentMethods.getResults().get(0).getId());
    assertEquals("Mastercard Débito", paymentMethods.getResults().get(0).getName());
    assertEquals("debit_card", paymentMethods.getResults().get(0).getPaymentTypeId());
    assertEquals("testing", paymentMethods.getResults().get(0).getStatus());
    assertEquals(THUMBNAIL, paymentMethods.getResults().get(0).getSecureThumbnail());
    assertEquals(THUMBNAIL, paymentMethods.getResults().get(0).getThumbnail());
    assertEquals("unsupported", paymentMethods.getResults().get(0).getDeferredCapture());
    assertEquals(1, paymentMethods.getResults().get(0).getSettings().size());
    assertEquals(
        "standard",
        paymentMethods.getResults().get(0).getSettings().get(0).getCardNumber().getValidation());
    assertEquals(
        16, paymentMethods.getResults().get(0).getSettings().get(0).getCardNumber().getLength());
    assertEquals(
        "^(502121)", paymentMethods.getResults().get(0).getSettings().get(0).getBin().getPattern());
    assertNull(
        paymentMethods.getResults().get(0).getSettings().get(0).getBin().getInstallmentsPattern());
    assertNull(
        paymentMethods.getResults().get(0).getSettings().get(0).getBin().getExclusionPattern());
    assertEquals(
        3, paymentMethods.getResults().get(0).getSettings().get(0).getSecurityCode().getLength());
    assertEquals(
        "back",
        paymentMethods
            .getResults()
            .get(0)
            .getSettings()
            .get(0)
            .getSecurityCode()
            .getCardLocation());
    assertEquals(
        "mandatory",
        paymentMethods.getResults().get(0).getSettings().get(0).getSecurityCode().getMode());
    assertEquals(3, paymentMethods.getResults().get(0).getAdditionalInfoNeeded().size());
    assertTrue(
        paymentMethods
            .getResults()
            .get(0)
            .getAdditionalInfoNeeded()
            .containsAll(additionalInfoNeeded));
    assertEquals(new BigDecimal("0.5"), paymentMethods.getResults().get(0).getMinAllowedAmount());
    assertEquals(new BigDecimal("60000"), paymentMethods.getResults().get(0).getMaxAllowedAmount());
    assertEquals(ACCREDITATION_TIME, paymentMethods.getResults().get(0).getAccreditationTime());
    assertTrue(paymentMethods.getResults().get(0).getFinancialInstitutions().isEmpty());
    assertTrue(paymentMethods.getResults().get(0).getProcessingModes().contains("aggregator"));
  }
}
