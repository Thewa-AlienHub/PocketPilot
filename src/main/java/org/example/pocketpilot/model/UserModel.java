package org.example.pocketpilot.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UserModel {
    private ObjectId id;
    private String name;
    private String email;
    private String password;
    private String userName;
    private String userRole;
}
