package com.aayush.easybuyuserservice.dto.response;

import com.aayush.easybuyuserservice.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(

        UUID id,

        String firstName,

        String lastName,

        String email,

        String phoneNumber,

        String address,

        Role role,

        Boolean enabled,

        Instant createdAt,

        Instant updatedAt
) {}