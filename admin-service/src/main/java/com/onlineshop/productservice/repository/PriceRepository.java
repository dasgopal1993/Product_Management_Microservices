package com.onlineshop.productservice.repository;

import com.onlineshop.productservice.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
