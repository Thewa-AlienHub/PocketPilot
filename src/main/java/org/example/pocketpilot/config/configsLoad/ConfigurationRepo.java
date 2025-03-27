package org.example.pocketpilot.config.configsLoad;

import org.example.pocketpilot.entities.Configs.ConfigurationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Async;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ConfigurationRepo extends MongoRepository<ConfigurationEntity, String> {

    @Async
    CompletableFuture<List<ConfigurationEntity>> findAllBy();  // ✅ Ensure it returns a Future List
}

