package com.aayush.easybuyuserservice.service;


import com.aayush.easybuyuserservice.dto.request.CreateUserRequest;
import com.aayush.easybuyuserservice.dto.request.UpdateUserRequest;
import com.aayush.easybuyuserservice.dto.response.UserResponse;
import com.aayush.easybuyuserservice.entity.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    UserResponse changeUserRole(UUID id, Role role);

}