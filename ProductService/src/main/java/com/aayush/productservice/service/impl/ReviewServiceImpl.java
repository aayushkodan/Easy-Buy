package com.aayush.productservice.service.impl;

import com.aayush.productservice.dto.request.UpdateReviewRequest;
import com.aayush.productservice.dto.response.ReviewResponse;
import com.aayush.productservice.entity.Review;
import com.aayush.productservice.exception.EasyBuyException;
import com.aayush.productservice.exception.ErrorCode;
import com.aayush.productservice.mapper.ReviewMapper;
import com.aayush.productservice.repository.ReviewRepository;
import com.aayush.productservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getAllReviews() {

        log.debug("Fetching all reviews");

        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ReviewResponse getReviewById(Long reviewId) {

        log.debug("Fetching review with id: {}", reviewId);

        return reviewMapper.toResponse(findReview(reviewId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> getReviewsByProductId(UUID productId) {

        log.debug("Fetching reviews for product id: {}", productId);

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public ReviewResponse updateReview(
            Long reviewId,
            UpdateReviewRequest request
    ) {

        log.info("Updating review with id: {}", reviewId);

        Review review = findReview(reviewId);

        reviewMapper.updateReview(request, review);

        Review updatedReview = reviewRepository.save(review);

        log.info("Review updated successfully with id: {}", reviewId);

        return reviewMapper.toResponse(updatedReview);
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId) {

        log.warn("Deleting review with id: {}", reviewId);

        Review review = findReview(reviewId);

        reviewRepository.delete(review);

        log.info("Review deleted successfully with id: {}", reviewId);
    }

    private Review findReview(Long reviewId) {

        return reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new EasyBuyException(
                                ErrorCode.REVIEW_NOT_FOUND,
                                "Review not found with id: " + reviewId
                        )
                );
    }
}