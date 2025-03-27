package org.example.pocketpilot.entities.Configs;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "configuration")
@Data
public class ConfigurationEntity {
    @Id
    private ObjectId id;
    private int enumId;  // Stores Enum ID
    private String type; // Enum Type: USER_ROLE or TRANSACTION_CATEGORY
    private String value; // Enum Value (e.g., "Admin", "Food")


}
