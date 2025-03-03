package org.example.pocketpilot.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class UserEntity {
    @Id
    private ObjectId id;

    private String name;
    private String email;
    private String password;
    private String userName;
    private String userRole;
}

