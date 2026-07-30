package com.ganesh.mandal.gateway;

import com.ganesh.mandal.dto.GatewayConfigDTO;
import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayProvider {

    String getProviderName();

    Map<String, Object> createOrder(BigDecimal amount, String receiptNumber, String notes);

    Map<String, Object> verifyPayment(String orderId, String paymentId, String signature);

    Map<String, Object> fetchPaymentStatus(String paymentId);

    boolean verifyWebhook(String payload, String signature, String webhookSecret);

    GatewayConfigDTO getConfig();
}
