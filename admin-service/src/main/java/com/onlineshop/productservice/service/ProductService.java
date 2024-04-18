package com.onlineshop.productservice.service;

import com.onlineshop.productservice.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> createProducts(List<ProductDto> productDto);

    List<ProductDto> getAllInventoryLimitedOrUnavailable();

    List<ProductDto> getProductsByCategory(String category);

    List<ProductDto> getProductsByCategoryAndSort(String category, SortOption sortOption);

    ProductDto getProductById(Long productId);
    List<ProductDto> getAllProducts();
    ProductDto updateProduct(ProductDto productDto);
    void deleteProduct(Long productId);

}
