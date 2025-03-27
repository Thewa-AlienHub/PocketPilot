package org.example.pocketpilot.config.configsLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/config")
public class ConfigsLoadController {
    @Autowired
    private ConfigurationService configService;

    @GetMapping("/roles")
    public CompletableFuture<ResponseEntity<Map<Integer, String>>> getUserRoles() {
        return configService.getConfigsByType("USER_ROLE")
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/categories")
    public CompletableFuture<ResponseEntity<Map<Integer, String>>> getTransactionCategories() {
        return configService.getConfigsByType("TRANSACTION_CATEGORY")
                .thenApply(ResponseEntity::ok);
    }

}
