package com.ganesh.mandal.gateway;

import com.ganesh.mandal.dto.GatewayConfigDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RazorpayPaymentGateway implements PaymentGatewayProvider {

    @Value("${payment.razorpay.key-id:rzp_test_placeholder_key}")
    private String keyId;

    @Value("${payment.razorpay.key-secret:rzp_test_placeholder_secret}")
    private String keySecret;

    @Value("${payment.razorpay.webhook-secret:whsec_placeholder}")
    private String webhookSecret;

    @Value("${payment.razorpay.enabled:false}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled) {
            log.info("Razorpay gateway initialized with key: {}", keyId);
        } else {
            log.info("Razorpay gateway is DISABLED. Set payment.razorpay.enabled=true to activate.");
        }
    }

    @Override
    public String getProviderName() {
        return "RAZORPAY";
    }

    @Override
    public Map<String, Object> createOrder(BigDecimal amount, String receiptNumber, String notes) {
        Map<String, Object> result = new HashMap<>();

        if (!enabled) {
            log.warn("Razorpay is disabled. Generating mock order for testing.");
            result.put("id", "mock_order_" + System.currentTimeMillis());
            result.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
            result.put("currency", "INR");
            result.put("status", "created");
            result.put("key_id", keyId);
            return result;
        }

        try {
            String url = "https://api.razorpay.com/v1/orders";
            String body = String.format(
                "amount=%d&currency=INR&receipt=%s&notes[description]=%s",
                amount.multiply(BigDecimal.valueOf(100)).longValue(),
                receiptNumber,
                notes != null ? notes : "Donation"
            );

            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String auth = keyId + ":" + keySecret;
            conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes());

            int responseCode = conn.getResponseCode();
            String responseBody = new String(
                (responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream())
                    .readAllBytes()
            );
            conn.disconnect();

            if (responseCode >= 200 && responseCode < 300) {
                com.fasterxml.jackson.databind.JsonNode node =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
                result.put("id", node.get("id").asText());
                result.put("amount", node.get("amount").asLong());
                result.put("currency", node.get("currency").asText());
                result.put("status", node.get("status").asText());
                result.put("key_id", keyId);
                log.info("Razorpay order created: {}", node.get("id").asText());
            } else {
                log.error("Razorpay order creation failed: {} {}", responseCode, responseBody);
                throw new RuntimeException("Razorpay order creation failed: " + responseBody);
            }
        } catch (Exception e) {
            log.error("Razorpay order creation error: {}", e.getMessage());
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) {
        Map<String, Object> result = new HashMap<>();
        result.put("order_id", orderId);
        result.put("payment_id", paymentId);
        result.put("signature", signature);

        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(), "HmacSHA256"));
            byte[] hashBytes = mac.doFinal(payload.getBytes());
            String expectedSignature = bytesToHex(hashBytes);
            boolean valid = expectedSignature.equals(signature);
            result.put("signature_valid", valid);
            log.info("Razorpay signature verification for {}: {}", paymentId, valid ? "VALID" : "INVALID");
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            result.put("signature_valid", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> fetchPaymentStatus(String paymentId) {
        Map<String, Object> result = new HashMap<>();

        if (!enabled) {
            result.put("id", paymentId);
            result.put("status", "captured");
            result.put("error_reason", null);
            return result;
        }

        try {
            String url = "https://api.razorpay.com/v1/payments/" + paymentId;
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            String auth = keyId + ":" + keySecret;
            conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));

            int responseCode = conn.getResponseCode();
            String responseBody = new String(
                (responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream())
                    .readAllBytes()
            );
            conn.disconnect();

            if (responseCode >= 200 && responseCode < 300) {
                com.fasterxml.jackson.databind.JsonNode node =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
                result.put("id", node.get("id").asText());
                result.put("status", node.get("status").asText());
                result.put("amount", node.get("amount").asLong());
                result.put("currency", node.get("currency").asText());
                result.put("method", node.has("method") ? node.get("method").asText() : null);
                result.put("error_reason", node.has("error_reason") && !node.get("error_reason").isNull()
                    ? node.get("error_reason").asText() : null);
                result.put("error_description", node.has("error_description") && !node.get("error_description").isNull()
                    ? node.get("error_description").asText() : null);
                result.put("raw_response", responseBody);
            } else {
                log.error("Razorpay fetch payment failed: {} {}", responseCode, responseBody);
                result.put("error", responseBody);
                result.put("status", "unknown");
            }
        } catch (Exception e) {
            log.error("Razorpay fetch payment error: {}", e.getMessage());
            result.put("error", e.getMessage());
            result.put("status", "unknown");
        }
        return result;
    }

    @Override
    public boolean verifyWebhook(String payload, String signature, String webhookSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            String secret = webhookSecret != null ? webhookSecret : this.webhookSecret;
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] hashBytes = mac.doFinal(payload.getBytes());
            String expectedSignature = bytesToHex(hashBytes);
            boolean valid = expectedSignature.equals(signature);
            log.info("Razorpay webhook signature verification: {}", valid ? "VALID" : "INVALID");
            return valid;
        } catch (Exception e) {
            log.error("Webhook signature verification error: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public GatewayConfigDTO getConfig() {
        return GatewayConfigDTO.builder()
                .provider(getProviderName())
                .keyId(keyId)
                .keySecret(keySecret)
                .webhookSecret(webhookSecret)
                .enabled(enabled)
                .build();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
