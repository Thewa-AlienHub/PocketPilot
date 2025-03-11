package org.example.pocketpilot.utils;

import lombok.RequiredArgsConstructor;
import org.example.pocketpilot.components.CurrencyProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurencyConversionService {

    private final RestTemplate restTemplate;
    private final CurrencyProperties currencyProperties;


    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {

        String API_URL = currencyProperties.getApiUrl();
        String API_KEY = currencyProperties.getApiKey();

        String url = API_URL +API_KEY+"/latest/"+ fromCurrency;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        System.out.println(response);

        if (response == null || !response.containsKey("conversion_rates")) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        Map<String, Double> rates = (Map<String, Double>) response.get("conversion_rates");
        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException("Invalid currency code: " + toCurrency);
        }

        BigDecimal exchangeRate = BigDecimal.valueOf(rates.get(toCurrency));
        return amount.multiply(exchangeRate);
    }
}
