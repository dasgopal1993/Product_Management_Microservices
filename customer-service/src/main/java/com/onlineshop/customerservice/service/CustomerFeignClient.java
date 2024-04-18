package com.onlineshop.customerservice.service;

import com.onlineshop.customerservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ADMIN-SERVICE")
public interface CustomerFeignClient {

    @GetMapping("api/products/admin")
    public List<ProductDto> getAllProducts();
    @GetMapping("api/products/admin/{id}")
    public ProductDto getProductById(@PathVariable("id") Long productId);
}
