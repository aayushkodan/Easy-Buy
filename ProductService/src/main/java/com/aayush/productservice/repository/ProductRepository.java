package com.aayush.productservice.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.aayush.productservice.dto.response.ProductResponse;
import com.aayush.productservice.entity.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

}
