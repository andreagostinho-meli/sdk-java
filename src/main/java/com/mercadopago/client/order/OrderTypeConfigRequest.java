package com.mercadopago.client.order;

import lombok.Builder;
import lombok.Getter;

/** OrderTypeConfigRequest class. */
@Getter
@Builder
public class OrderTypeConfigRequest {

    /** Capture mode. */
    private String captureMode;

    /** IP Address. */
    private String ipAddress;

    /** URL where MercadoPago does the final redirect. */
    private String callbackUrl;
}
