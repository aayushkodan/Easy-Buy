package com.aayush.easybuyuserservice.dto.request;

import jakarta.validation.constraints.*;

public record CreateUserRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Pattern(
                regexp = "\\d{10}$",
                message = "Phone number must contain 10 digits"
        )
        String phoneNumber,

        String address
) {}