package com.onlineshop.productservice.service;

import com.onlineshop.productservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface AdminFeignClient {

    @GetMapping("api/products/customer/category/{category}")
    public List<ProductDto> getProductsByCategory(@PathVariable String category);

    @GetMapping("api/products/customer/category/{category}/sort/{sortOption}")
    public List<ProductDto> getProductsByCategoryAndSort(@PathVariable String category, @PathVariable SortOption sortOption);

}
