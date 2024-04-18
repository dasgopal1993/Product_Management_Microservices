package com.onlineshop.customerservice.controller;

import com.onlineshop.customerservice.dto.ProductDto;
import com.onlineshop.customerservice.exception.ProductServiceException;
import com.onlineshop.customerservice.service.CustomerService;
import com.onlineshop.customerservice.service.SortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/products/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of all products retrieved successfully")
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
            List<ProductDto> products = customerService.getAllProducts();
            return ResponseEntity.ok(products);

    }

    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
            ProductDto productDto = customerService.getProductById(productId);
            return ResponseEntity.ok(productDto);

    }

    @Operation(summary = "Get products by category")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable @Parameter(description = "Category name")  String category) {

            List<ProductDto> productDtos = customerService.getProductsByCategory(category);
            return ResponseEntity.ok(productDtos);

    }

    @Operation(summary = "Get products by category and sort option")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully by category and sort option")
    @GetMapping("/category/{category}/sort/{sortOption}")
    public ResponseEntity<?> getProductsByCategoryAndSort(@PathVariable @Parameter(description = "Category name") String category, @PathVariable @Parameter(description = "Sort option: INVENTORY or PRICE") SortOption sortOption) {

            List<ProductDto> productDtos = customerService.getProductsByCategoryAndSort(category, sortOption);
            return ResponseEntity.ok(productDtos);

    }
}
