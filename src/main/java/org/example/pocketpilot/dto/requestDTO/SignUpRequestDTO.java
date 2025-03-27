package org.example.pocketpilot.dto.requestDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotNull(message = "User Role is required")
    private int userRole;

    @NotBlank(message = "Currency code is required")
    private String currencyCode;

}
