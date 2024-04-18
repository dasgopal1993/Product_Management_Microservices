package com.onlineshop.productservice.repository;

import com.onlineshop.productservice.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
