package com.sushilk.payment_service.services;

import com.sushilk.payment_service.enums.PaymentProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentProviderFactory {

    private final Map<PaymentProvider, PaymentProviderService> providerMap;

    public PaymentProviderFactory(List<PaymentProviderService> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(
                        PaymentProviderService::getProvider, // Enum key
                        Function.identity()
                ));
    }

    public PaymentProviderService getProvider(PaymentProvider provider) {
        PaymentProviderService service = providerMap.get(provider);
        if (service == null) {
            throw new IllegalArgumentException("No provider found for: " + provider);
        }
        return service;
    }
}





