package com.aayush.productservice.mapper;

import com.aayush.productservice.dto.request.AddReviewRequest;
import com.aayush.productservice.dto.request.UpdateReviewRequest;
import com.aayush.productservice.dto.response.ReviewResponse;
import com.aayush.productservice.entity.Review;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewResponse toResponse(Review review);

    Review addReviewToEntity(AddReviewRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReview(UpdateReviewRequest request, @MappingTarget Review review);
}
