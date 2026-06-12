package com.aayush.easybuyuserservice.dto.request;

import com.aayush.easybuyuserservice.entity.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(

        @NotNull(message = "Role is required")
        Role role

) {
}