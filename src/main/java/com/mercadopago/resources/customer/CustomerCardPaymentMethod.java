package com.mercadopago.resources.customer;

import lombok.Data;

/** Payment method details related to a customer card. */
@Data
public class CustomerCardPaymentMethod {
  /** Id of the payment method. */
  private String id;

  /** Name of payment method. */
  private String name;

  /** Type of payment method. */
  private String paymentTypeId;

  /** Thumbnail of payment method. */
  private String thumbnail;

  /** Thumbnail of payment method from a secure source. */
  private String secureThumbnail;
}
