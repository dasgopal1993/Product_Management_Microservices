package com.onlineshop.customerservice.controller;

import com.onlineshop.customerservice.dto.AttributeDto;
import com.onlineshop.customerservice.dto.InventoryDto;
import com.onlineshop.customerservice.dto.PriceDto;
import com.onlineshop.customerservice.dto.ProductDto;
import com.onlineshop.customerservice.service.SortOption;
import com.onlineshop.customerservice.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerServiceImpl customerService;

    List<ProductDto> productDtos;
    ProductDto productDto;

    @BeforeEach
    void setUp(){

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
    public void testGetAllProductsSuccess() throws Exception {

        doReturn(productDtos).when(customerService).getAllProducts();

        mockMvc.perform(get("/api/products/customer")
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
    public void testGetProductByIdSuccess() throws Exception {

        doReturn(productDto).when(customerService).getProductById(productDto.getProductId());

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/products/customer/{id}", productDto.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productDto.getProductId()))
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.brand").value("Brand 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andReturn();
    }

    @Test
    public void testGetAllProductsCategorySuccess() throws Exception {

        String category="product";
        doReturn(productDtos).when(customerService).getProductsByCategory(category);

        mockMvc.perform(get("/api/products/customer/category/{category}", category)
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

        doReturn(productDtos).when(customerService).getProductsByCategoryAndSort(category, sortOption);

        mockMvc.perform(get("/api/products/customer/category/{category}/sort/{sortOption}", category, sortOption)
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
