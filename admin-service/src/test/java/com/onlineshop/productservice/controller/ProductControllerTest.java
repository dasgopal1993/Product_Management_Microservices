package com.onlineshop.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshop.productservice.dto.AttributeDto;
import com.onlineshop.productservice.dto.InventoryDto;
import com.onlineshop.productservice.dto.PriceDto;
import com.onlineshop.productservice.dto.ProductDto;
import com.onlineshop.productservice.entity.Attribute;
import com.onlineshop.productservice.entity.Inventory;
import com.onlineshop.productservice.entity.Price;
import com.onlineshop.productservice.entity.Product;
import com.onlineshop.productservice.exception.ProductServiceException;
import com.onlineshop.productservice.service.ProductService;
import com.onlineshop.productservice.service.SortOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @InjectMocks
    ProductController productController;


    List<ProductDto> productDtos;
    List<Product> products;

    Product product;
    ProductDto productDto;

    @BeforeEach
    void setUp(){

        product = new Product(1L, "Product 1", "Brand 1", "Description 1",
                new Price(1L, "USD", new BigDecimal("49.99")),
                new Inventory(1L, 100, 80, 20),
                Arrays.asList(new Attribute(1L, "Color", "Red")));

        products = Arrays.asList(
                new Product(1L, "Product 1", "Brand 1", "Description 1",
                        new Price(1L, "USD", new BigDecimal("49.99")),
                        new Inventory(1L, 100, 80, 20),
                        Arrays.asList(new Attribute(1L, "Color", "Red"))),
                new Product(2L, "Product 2", "Brand 2", "Description 2",
                        new Price(2L, "USD", new BigDecimal("59.99")),
                        new Inventory(2L, 150, 100, 50),
                        Arrays.asList(new Attribute(2L, "Size", "Large")))
        );

        productDto = new ProductDto(1L, "Product 1", "Brand 1", "Description 1",
                new PriceDto(1L, "USD", new BigDecimal("49.99")),
                new InventoryDto(1L, 100, 80, 20),
                Arrays.asList(new AttributeDto(1L, "Color", "Red")));

        productDtos = Arrays.asList(
                new ProductDto(1L, "Product 1", "Brand 1", "Description 1",
                        new PriceDto(1L, "USD", new BigDecimal("49.99")),
                        new InventoryDto(1L, 100, 80, 20),
                        Arrays.asList(new AttributeDto(1L, "Color", "Red"))),
                new ProductDto(2L, "Product 2", "Brand 2", "Description 2",
                        new PriceDto(2L, "USD", new BigDecimal("59.99")),
                        new InventoryDto(2L, 150, 100, 50),
                        Arrays.asList(new AttributeDto(2L, "Size", "Large")))
        );
    }

    @Test
    public void testSaveProductSuccess() throws Exception{

        // Mocking the productService.createProducts() method within the test method
        doReturn(productDtos).when(productService).createProducts(productDtos);

       mockMvc.perform(post("/api/products/admin").contentType(MediaType.APPLICATION_JSON)
               .content(new ObjectMapper().writeValueAsString(productDtos))).andExpect(status().isCreated());

    }


    @Test
    public void testGetAllProductsSuccess() throws Exception {

        doReturn(productDtos).when(productService).getAllProducts();

        mockMvc.perform(get("/api/products/admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].brand").value("Brand 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].price.priceId").value(1L))
                .andExpect(jsonPath("$[0].price.currency").value("USD"))
                .andExpect(jsonPath("$[0].price.amount").value(49.99))
                .andExpect(jsonPath("$[0].inventory.inventoryId").value(1L))
                .andExpect(jsonPath("$[0].inventory.total").value(100))
                .andExpect(jsonPath("$[0].inventory.available").value(80))
                .andExpect(jsonPath("$[0].inventory.reserved").value(20))
                .andExpect(jsonPath("$[0].attributes[0].attributeId").value(1L))
                .andExpect(jsonPath("$[0].attributes[0].name").value("Color"))
                .andExpect(jsonPath("$[0].attributes[0].value").value("Red"));

    }

    @Test
    void testGetAllProducts_Failure() throws Exception {
        // Mock productService to throw an exception
        when(productService.getAllProducts()).thenThrow(new ProductServiceException("Failed to fetch products", new Throwable()));

        mockMvc.perform(get("/api/products/admin"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("An error occurred while processing the request. Please try again later."))
                .andExpect(jsonPath("$.errorCode").value("PRODUCT_SERVICE_UNAVAILABLE_OR_ISSUE"));
    }

    @Test
    public void testGetAllInventoryLimitedOrUnavailableSuccess() throws Exception {

        productDtos = Arrays.asList(
                new ProductDto(1L, "Product 1", "Brand 1", "Description 1",
                        new PriceDto(1L, "USD", new BigDecimal("49.99")),
                        new InventoryDto(1L, 10, 4, 6),
                        Arrays.asList(new AttributeDto(1L, "Color", "Red"))),
                new ProductDto(2L, "Product 2", "Brand 2", "Description 2",
                        new PriceDto(2L, "USD", new BigDecimal("59.99")),
                        new InventoryDto(2L, 5, 3, 2),
                        Arrays.asList(new AttributeDto(2L, "Size", "Large")))
        );

        doReturn(productDtos).when(productService).getAllInventoryLimitedOrUnavailable();

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/products/admin/inventory-limited-or-unavailable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].brand").value("Brand 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].price.priceId").value(1L))
                .andExpect(jsonPath("$[0].price.currency").value("USD"))
                .andExpect(jsonPath("$[0].price.amount").value(49.99))
                .andExpect(jsonPath("$[0].inventory.inventoryId").value(1L))
                .andExpect(jsonPath("$[0].inventory.total").value(10))
                .andExpect(jsonPath("$[0].inventory.available").value(4))
                .andExpect(jsonPath("$[0].inventory.reserved").value(6))
                .andExpect(jsonPath("$[0].attributes[0].attributeId").value(1L))
                .andExpect(jsonPath("$[0].attributes[0].name").value("Color"))
                .andExpect(jsonPath("$[0].attributes[0].value").value("Red"))
                .andExpect(jsonPath("$[1].productId").value(2L))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].brand").value("Brand 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].price.priceId").value(2L))
                .andExpect(jsonPath("$[1].price.currency").value("USD"))
                .andExpect(jsonPath("$[1].price.amount").value(59.99))
                .andExpect(jsonPath("$[1].inventory.inventoryId").value(2L))
                .andExpect(jsonPath("$[1].inventory.total").value(5))
                .andExpect(jsonPath("$[1].inventory.available").value(3))
                .andExpect(jsonPath("$[1].inventory.reserved").value(2))
                .andExpect(jsonPath("$[1].attributes[0].attributeId").value(2L))
                .andExpect(jsonPath("$[1].attributes[0].name").value("Size"))
                .andExpect(jsonPath("$[1].attributes[0].value").value("Large"))
                .andReturn();
    }

    @Test
    public void testGetAllInventoryLimitedOrUnavailableEmpty() throws Exception {

        doReturn(new ArrayList<>()).when(productService).getAllInventoryLimitedOrUnavailable();

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/products/admin/inventory-limited-or-unavailable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    public void testGetProductByIdSuccess() throws Exception {

        doReturn(productDto).when(productService).getProductById(productDto.getProductId());

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/products/admin/{id}", productDto.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productDto.getProductId()))
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.brand").value("Brand 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andReturn();
    }

    @Test
    void testGetProductByIdFailure() throws Exception {
        Long productId = 6L;

        // Mock productService to throw an exception
        when(productService.getProductById(productId)).thenThrow(new ProductServiceException("Product not found", new Throwable()));

        mockMvc.perform(get("/api/products/admin/{id}", productId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Failed to retrieve product with ID: " + productId + ". Product not found"));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        Long productId = 1L;
        ProductDto productDto1 = productDto;
        productDto1.setName("Updated Product");

        // Mock productService.updateProduct method
        when(productService.updateProduct(any(ProductDto.class))).thenReturn(productDto);

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/products/admin/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\"}"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
    }


    @Test
    void testDeleteProduct_Success() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/api/products/admin/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Product successfully deleted!"));

        // Verify that productService.deleteProduct was called with the correct argument
        verify(productService).deleteProduct(productId);
    }

    @Test
    void testDeleteProduct_Exception() throws Exception {
        // Given
        Long productId = 1L;

        // Mocking productService to throw an exception
        doThrow(new ProductServiceException("Delete failed", new Throwable())).when(productService).deleteProduct(productId);

        mockMvc.perform(delete("/api/products/admin/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete product: Delete failed"));
    }

    @Test
    public void testGetAllProductsCategorySuccess() throws Exception {

        String category="product";
        doReturn(productDtos).when(productService).getProductsByCategory(category);

        mockMvc.perform(get("/api/products/admin/category/{category}", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].brand").value("Brand 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].price.priceId").value(1L))
                .andExpect(jsonPath("$[0].price.currency").value("USD"))
                .andExpect(jsonPath("$[0].price.amount").value(49.99))
                .andExpect(jsonPath("$[0].inventory.inventoryId").value(1L))
                .andExpect(jsonPath("$[0].inventory.total").value(100))
                .andExpect(jsonPath("$[0].inventory.available").value(80))
                .andExpect(jsonPath("$[0].inventory.reserved").value(20))
                .andExpect(jsonPath("$[0].attributes[0].attributeId").value(1L))
                .andExpect(jsonPath("$[0].attributes[0].name").value("Color"))
                .andExpect(jsonPath("$[0].attributes[0].value").value("Red"));

    }

    @Test
    public void testGetProductsByCategoryAndSort() throws Exception {

        String category="product";
        SortOption sortOption = SortOption.INVENTORY;

        doReturn(productDtos).when(productService).getProductsByCategoryAndSort(category, sortOption);

        mockMvc.perform(get("/api/products/admin/category/{category}/sort/{sortOption}", category, sortOption)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].brand").value("Brand 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].price.priceId").value(1L))
                .andExpect(jsonPath("$[0].price.currency").value("USD"))
                .andExpect(jsonPath("$[0].price.amount").value(49.99))
                .andExpect(jsonPath("$[0].inventory.inventoryId").value(1L))
                .andExpect(jsonPath("$[0].inventory.total").value(100))
                .andExpect(jsonPath("$[0].inventory.available").value(80))
                .andExpect(jsonPath("$[0].inventory.reserved").value(20))
                .andExpect(jsonPath("$[0].attributes[0].attributeId").value(1L))
                .andExpect(jsonPath("$[0].attributes[0].name").value("Color"))
                .andExpect(jsonPath("$[0].attributes[0].value").value("Red"));

    }

}

