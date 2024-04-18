package com.onlineshop.productservice.controller;

import com.onlineshop.productservice.dto.ProductDto;
import com.onlineshop.productservice.exception.ProductServiceException;
import com.onlineshop.productservice.service.ProductService;
import com.onlineshop.productservice.service.SortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Management", description = "Endpoints for managing products")
@RestController
@RequestMapping("api/products/admin")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @Operation(summary = "Save multiple products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Products saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    //Build Save Product REST API
    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody List<ProductDto> productDto) {
            List<ProductDto> savedProductDtos = productService.createProducts(productDto);
            return new ResponseEntity<>(savedProductDtos, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productDtos = productService.getAllProducts();
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    @Operation(summary = "Get products by inventory-limited-or-unavailable")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully by inventory-limited-or-unavailable")
    @GetMapping("/inventory-limited-or-unavailable")
    public ResponseEntity<List<ProductDto>> getAllInventoryLimitedOrUnavailable() {
        List<ProductDto> products = productService.getAllInventoryLimitedOrUnavailable();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            ProductDto productDto = productService.getProductById(productId);
            return ResponseEntity.ok(productDto);
        } catch (ProductServiceException ex) {
            String errorMessage = "Failed to retrieve product with ID: " + productId + ". " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @Operation(summary = "Update product by ID")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto productDto) {
            productDto.setProductId(productId);
            ProductDto updatedProductDto = productService.updateProduct(productDto);
            return ResponseEntity.ok(updatedProductDto);
    }

    @Operation(summary = "Delete product by ID")
    @ApiResponse(responseCode = "200", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Product successfully deleted!");
        } catch (ProductServiceException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product: " + ex.getMessage());
        }
    }

    @Operation(summary = "Get products by category")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully by category")
    @GetMapping("/category/{category}")
    public List<ProductDto> getProductsByCategory(@PathVariable @Parameter(description = "Category name") String category) {
        return productService.getProductsByCategory(category);
    }

    @Operation(summary = "Get products by category and sort option")
    @ApiResponse(responseCode = "200", description = "List of products retrieved successfully by category and sort option")
    @GetMapping("/category/{category}/sort/{sortOption}")
    public List<ProductDto> getProductsByCategoryAndSort(@PathVariable @Parameter(description = "Category name") String category, @PathVariable @Parameter(description = "Sort option: 'INVENTORY'  or 'PRICE' ") SortOption sortOption) {
        return productService.getProductsByCategoryAndSort(category, sortOption);
    }
}
