package com.aayush.productservice.service;

import com.aayush.productservice.dto.request.UpdateReviewRequest;
import com.aayush.productservice.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    List<ReviewResponse> getAllReviews();

    ReviewResponse getReviewById(Long reviewId);

    List<ReviewResponse> getReviewsByProductId(UUID productId);

    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);
}