// src/main/java/com/femmora/productservice/repository/ProductRepository.java
package com.femmora.productservice.repository;

import com.femmora.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Spring Boot automatically implements this!
}