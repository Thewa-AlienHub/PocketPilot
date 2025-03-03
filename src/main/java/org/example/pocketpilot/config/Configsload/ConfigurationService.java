package org.example.pocketpilot.config.Configsload;

import org.example.pocketpilot.entities.Configs.ConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepo configurationRepo;

    @Async
    public CompletableFuture<Map<Integer, String>> getConfigsByType(String type) {
        return configurationRepo.findAllBy()
                .thenApply(configs -> configs == null ? Map.of() : configs.stream()
                        .filter(config -> Objects.equals(config.getType(), type))  // Safe check for null
                        .collect(Collectors.toMap(ConfigurationEntity::getEnumId, ConfigurationEntity::getValue)));
    }
}
