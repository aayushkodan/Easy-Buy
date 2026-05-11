package com.aayush.productservice.service.impl;

import com.aayush.productservice.exception.EasyBuyException;
import com.aayush.productservice.exception.ErrorCode;
import com.aayush.productservice.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageAwsS3StorageServiceImpl implements ImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) throws IOException {

        validateFile(file);

        String originalFilename =
                file.getOriginalFilename() == null
                        ? "image"
                        : sanitizeFilename(file.getOriginalFilename());

        String objectKey =
                "products/" +
                        UUID.randomUUID() +
                        "-" +
                        originalFilename;

        log.info(
                "Uploading file {} to S3 bucket {}",
                originalFilename,
                bucketName
        );

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(file.getContentType())
                        .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        log.info(
                "Successfully uploaded file with key {}",
                objectKey
        );

        return objectKey;
    }

    @Override
    public void delete(String objectKey) {

        log.warn(
                "Deleting image from S3 with key {}",
                objectKey
        );

        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        s3Client.deleteObject(deleteObjectRequest);

        log.info(
                "Successfully deleted image with key {}",
                objectKey
        );
    }

    @Override
    public String generatePresignedUrl(String objectKey) {

        log.debug(
                "Generating presigned URL for object key {}",
                objectKey
        );

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        String url =
                s3Presigner
                        .presignGetObject(presignRequest)
                        .url()
                        .toString();

        log.debug(
                "Generated presigned URL successfully for key {}",
                objectKey
        );

        return url;
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new EasyBuyException(
                    ErrorCode.INVALID_REQUEST,
                    "File cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new EasyBuyException(
                    ErrorCode.INVALID_REQUEST,
                    "File size exceeds 5 MB limit"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new EasyBuyException(
                    ErrorCode.INVALID_REQUEST,
                    "Only JPEG, PNG and WEBP images are allowed"
            );
        }
    }

    private String sanitizeFilename(String filename) {

        return filename
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-zA-Z0-9.-]", "");
    }
}