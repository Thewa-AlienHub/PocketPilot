package org.example.pocketpilot.components;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "currency")
@Getter
@Setter
public class CurrencyProperties {
    private String apiUrl;
    private String apiKey;
}
