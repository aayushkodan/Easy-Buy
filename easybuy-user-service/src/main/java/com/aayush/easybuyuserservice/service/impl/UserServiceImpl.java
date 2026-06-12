package com.aayush.easybuyuserservice.service.impl;

import com.aayush.easybuyuserservice.dto.request.CreateUserRequest;
import com.aayush.easybuyuserservice.dto.request.UpdateUserRequest;
import com.aayush.easybuyuserservice.dto.response.UserResponse;
import com.aayush.easybuyuserservice.entity.Role;
import com.aayush.easybuyuserservice.entity.User;
import com.aayush.easybuyuserservice.exception.EasybuyException;
import com.aayush.easybuyuserservice.exception.ErrorCode;
import com.aayush.easybuyuserservice.mapper.UserMapper;
import com.aayush.easybuyuserservice.repository.UserRepository;
import com.aayush.easybuyuserservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        log.info("Creating user with email={}", request.email());

        if (userRepository.existsByEmail(request.email())) {

            log.warn("User creation failed. Email already exists: {}", request.email());

            throw new EasybuyException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "User already exists with email: " + request.email()
            );
        }

        User user = userMapper.createToEntity(request);

        user.setEnabled(true);
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        log.info("User created successfully. userId={}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {

        log.debug("Fetching user by id={}", id);

        User user = findUser(id);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        log.debug("Fetching user by email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    log.warn("User not found. email={}", email);

                    return new EasybuyException(
                            ErrorCode.USER_NOT_FOUND,
                            "User not found with email: " + email
                    );
                });

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        log.debug("Fetching all users");

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        log.debug("Fetched {} users", users.size());

        return users;
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {

        log.info("Updating user. userId={}", id);

        User user = findUser(id);

        userMapper.updateUser(request, user);

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully. userId={}", id);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {

        log.info("Deleting user. userId={}", id);

        User user = findUser(id);

        userRepository.delete(user);

        log.info("User deleted successfully. userId={}", id);
    }

    @Override
    public UserResponse changeUserRole(UUID id, Role role) {

        log.info("Changing role for userId={} to role={}", id, role);

        User user = findUser(id);

        user.setRole(role);

        userRepository.save(user);

        log.info(
                "Role changed successfully. userId={}, newRole={}",
                id,
                role
        );

        return userMapper.toResponse(user);
    }
    private User findUser(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn("User deletion failed. User not found. userId={}", id);

                    return new EasybuyException(
                            ErrorCode.USER_NOT_FOUND,
                            "User not found with id: " + id
                    );
                });
    }
}