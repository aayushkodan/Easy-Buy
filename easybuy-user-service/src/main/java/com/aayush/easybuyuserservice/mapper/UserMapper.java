package com.aayush.easybuyuserservice.mapper;

import com.aayush.easybuyuserservice.dto.request.CreateUserRequest;
import com.aayush.easybuyuserservice.dto.request.UpdateUserRequest;
import com.aayush.easybuyuserservice.dto.response.UserResponse;
import com.aayush.easybuyuserservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User product);

    User createToEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UpdateUserRequest request, @MappingTarget User product);
}
