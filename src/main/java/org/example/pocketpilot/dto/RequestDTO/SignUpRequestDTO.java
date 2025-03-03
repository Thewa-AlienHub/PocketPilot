package org.example.pocketpilot.dto.RequestDTO;

import lombok.Data;

@Data
public class SignUpRequestDTO {

    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private int userRole;

}
