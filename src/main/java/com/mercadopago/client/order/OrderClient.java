package com.mercadopago.client.order;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.MercadoPagoClient;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.*;
import com.mercadopago.resources.order.Order;
import com.mercadopago.resources.order.OrderTransaction;
import com.mercadopago.serialization.Serializer;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static com.mercadopago.MercadoPagoConfig.getStreamHandler;

/** Client that use the Order API */
public class OrderClient extends MercadoPagoClient {
    private static final Logger LOGGER = Logger.getLogger(OrderClient.class.getName());

    private static final String URL_WITH_ID = "/v1/orders/%s";
    private static final String URL_PROCESS = URL_WITH_ID + "/process";
    private static final String URL_TRANSACTION = URL_WITH_ID + "/transactions";
    private static final String URL_CANCEL = URL_WITH_ID + "/cancel";

    /** Default constructor. Uses the default http client used by the SDK. */
    public OrderClient() {
        this(MercadoPagoConfig.getHttpClient());
    }

    /**
     * MercadoPagoClient constructor.
     *
     * @param httpClient http client
     */
    public OrderClient(MPHttpClient httpClient) {
        super(httpClient);
        StreamHandler streamHandler = getStreamHandler();
        streamHandler.setLevel(MercadoPagoConfig.getLoggingLevel());
        LOGGER.addHandler(streamHandler);
        LOGGER.setLevel(MercadoPagoConfig.getLoggingLevel());
    }

    /**
     * Method responsible for creating order with request options
     *
     * @param request request
     * @param requestOptions metadata to customize the request
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order create(OrderCreateRequest request, MPRequestOptions requestOptions)
            throws MPException, MPApiException {
        LOGGER.info("Sending order creation request");

        MPRequest mpRequest = MPRequest.builder()
                .uri("/v1/orders")
                .method(HttpMethod.POST)
                .payload(Serializer.serializeToJson(request))
                .build();

        MPResponse response = send(mpRequest, requestOptions);
        Order result = Serializer.deserializeFromJson(Order.class, response.getContent());
        result.setResponse(response);

        return result;
    }

    /**
     * Method responsible for creating order without request options
     *
     * @param request request
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order create(OrderCreateRequest request) throws MPException, MPApiException {
        return this.create(request, null);
    }

    /**
     * Method responsible for obtaining order by id
     *
     * @param id orderId
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order get(String id) throws MPException, MPApiException {
        return this.get(id, null);
    }

    /**
     * Method responsible for obtaining order by id with request options
     *
     * @param id orderId
     * @param requestOptions metadata to customize the request
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order get(String id, MPRequestOptions requestOptions) throws MPException, MPApiException {
        LOGGER.info("Sending order get request");

        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Order id cannot be null or empty");
        }

        String url = String.format(URL_WITH_ID, id);
        MPResponse response = send(url, HttpMethod.GET, null, null, requestOptions);

        Order order = Serializer.deserializeFromJson(Order.class, response.getContent());
        order.setResponse(response);

        return order;
    }

    /**
     * Method responsible for process an order by ID
     *
     * @param id orderId
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order process(String id) throws MPException, MPApiException {
        return this.process(id, null);
    }

    /**
     * Method responsible for process an order by ID with request options
     *
     * @param id orderId
     * @param requestOptions metadata to customize the request
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order process(String id, MPRequestOptions requestOptions) throws MPException, MPApiException {
        LOGGER.info("Sending order process request");

        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Order id cannot be null or empty");
        }

        String url = String.format(URL_PROCESS, id);
        MPResponse response = send(url, HttpMethod.POST, null, null, requestOptions);

        Order order = Serializer.deserializeFromJson(Order.class, response.getContent());
        order.setResponse(response);
        return order;
    }


     /**
     * Method responsible for creating order with request options
     *
     * @param orderId The ID of the order for which the transaction is created
     * @param request The request object containing transaction details
     * @param requestOptions Metadata to customize the request
     * @return The response for the order transaction
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public OrderTransaction createTransaction(String orderId, OrderTransactionRequest request,
                                                     MPRequestOptions requestOptions) throws MPException, MPApiException {
        LOGGER.info("Sending order transaction intent request");

        MPRequest mpRequest = MPRequest.builder()
                .uri(String.format(URL_TRANSACTION, orderId))
                .method(HttpMethod.POST)
                .payload(Serializer.serializeToJson(request))
                .build();

        MPResponse response = send(mpRequest, requestOptions);

        OrderTransaction order = Serializer.deserializeFromJson(OrderTransaction.class, response.getContent());
        order.setResponse(response);

        return order;
    }

     /**
     * Method responsible for creating a transaction for an order
     *
     * @param orderId The ID of the order for which the transaction is created
     * @param request The request object containing transaction details
     * @return The response for the order transaction
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public OrderTransaction createTransaction(String orderId, OrderTransactionRequest request)
            throws MPException, MPApiException {
        return this.createTransaction(orderId, request, null);
    }

    /**
     * Method responsible for cancel an order without request options
     *
     * @param orderId orderId
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order cancel(String orderId) throws MPException, MPApiException {
        return this.cancel(orderId, null);
    }

    /**
     * Method responsible for canceling an order by ID with request options
     *
     * @return order response
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public Order cancel(String orderId, MPRequestOptions requestOptions) throws MPException, MPApiException {
        LOGGER.info("Sending order to delete");

        if (StringUtils.isBlank(orderId)) {
            throw new IllegalArgumentException("Order id cannot be null or empty");
        }

        String url = String.format(URL_CANCEL, orderId);
        MPResponse response = send(url, HttpMethod.POST, null, null, requestOptions);
        
        Order order = Serializer.deserializeFromJson(Order.class, response.getContent());
        order.setResponse(response);
        
        return order;
    }

    /**
     * Method responsible for deleting a transaction from the Order
     *
     * @param orderId The ID of the order for which the transaction is created
     * @param transactionId The ID of the transaction to be retrieved
     * @param requestOptions Metadata to customize the request
     * @return The response for the action
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public OrderTransaction deleteTransaction(String orderId, String transactionId, MPRequestOptions requestOptions)
            throws MPException, MPApiException {
        LOGGER.info("Sending order transaction delete request");

        if (StringUtils.isBlank(orderId) || StringUtils.isBlank(transactionId)) {
            throw new IllegalArgumentException("Order id and Transaction id cannot be null or empty");
        }

        String url = String.format(URL_TRANSACTION, orderId) + "/%s";
        url = String.format(url, transactionId);
        LOGGER.fine("Delete transaction URL: " + url);

        MPResponse response = send(url, HttpMethod.DELETE, null, null, requestOptions);
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            return null;
        }
        try {
            return Serializer.deserializeFromJson(OrderTransaction.class, response.getContent());
        } catch (Exception e) {
            LOGGER.severe("Failed to deserialize response: " + e.getMessage());
            throw new MPApiException("Transaction not found", response);
        }
    }

    /**
     * Method responsible for deleting a transaction from the Order
     *
     * @param orderId The ID of the order for which the transaction is created
     * @param transactionId The ID of the transaction to be retrieved
     * @return The response for the action
     * @throws MPException an error if the request fails
     * @throws MPApiException an error if the request fails
     */
    public OrderTransaction deleteTransaction(String orderId, String transactionId)
            throws MPException, MPApiException {
        return this.deleteTransaction(orderId, transactionId, null);
    }
}
