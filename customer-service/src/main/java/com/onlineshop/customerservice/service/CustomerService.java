package com.onlineshop.customerservice.service;

import com.onlineshop.customerservice.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerService{

    List<ProductDto> getAllProducts();

    List<ProductDto> getProductsByCategory(String category);

    List<ProductDto> getProductsByCategoryAndSort(String category, SortOption sortBy);

    ProductDto getProductById(Long productId);

}
