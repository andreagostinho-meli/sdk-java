package com.mercadopago.resources.order;

import com.mercadopago.net.MPResource;
import lombok.Getter;

import java.util.List;

// API version: b950ae02-4f49-4686-9ad3-7929b21b6495

/** OrderTransaction class. */
@Getter
public class OrderTransaction extends MPResource{

    /** Payments information. */
    private List<OrderPayment> payments;

    /** Refunds information. */
    private List<OrderRefund> refunds;
}
