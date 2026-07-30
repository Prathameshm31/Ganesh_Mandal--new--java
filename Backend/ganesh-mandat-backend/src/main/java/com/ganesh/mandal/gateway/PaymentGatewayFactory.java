package com.ganesh.mandal.gateway;

import com.ganesh.mandal.entity.PaymentGateway;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayFactory {

    private final List<PaymentGatewayProvider> providers;
    private final Map<String, PaymentGatewayProvider> providerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (PaymentGatewayProvider provider : providers) {
            providerMap.put(provider.getProviderName().toUpperCase(), provider);
            log.info("Registered payment gateway provider: {}", provider.getProviderName());
        }
    }

    public PaymentGatewayProvider getProvider(String name) {
        if (name == null) return getDefaultProvider();
        PaymentGatewayProvider provider = providerMap.get(name.toUpperCase());
        if (provider == null) {
            log.warn("Payment gateway provider '{}' not found, using default", name);
            return getDefaultProvider();
        }
        return provider;
    }

    public PaymentGatewayProvider getProvider(PaymentGateway gateway) {
        return getProvider(gateway.name());
    }

    public PaymentGatewayProvider getDefaultProvider() {
        if (providerMap.isEmpty()) {
            throw new IllegalStateException("No payment gateway providers registered");
        }
        return providerMap.values().iterator().next();
    }

    public Map<String, PaymentGatewayProvider> getAllProviders() {
        return providerMap;
    }
}
