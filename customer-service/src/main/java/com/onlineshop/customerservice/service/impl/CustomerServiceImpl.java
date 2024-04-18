package com.onlineshop.customerservice.service.impl;

import com.onlineshop.customerservice.dto.AttributeDto;
import com.onlineshop.customerservice.dto.InventoryDto;
import com.onlineshop.customerservice.dto.PriceDto;
import com.onlineshop.customerservice.dto.ProductDto;
import com.onlineshop.customerservice.exception.ProductServiceException;
import com.onlineshop.customerservice.service.CustomerFeignClient;
import com.onlineshop.customerservice.service.CustomerService;
import com.onlineshop.customerservice.service.SortOption;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerFeignClient feignClient;


    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getAllProductsFallbackMethod")
    @Override
    public List<ProductDto> getAllProducts() {
            List<ProductDto> allProducts = feignClient.getAllProducts();
            return allProducts.stream()
                    .filter(productDto -> productDto.getInventory().getAvailable() > 5)
                    .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category) {
            List<ProductDto> allProducts = getAllProducts();
            return allProducts.stream()
                    .filter(productDto -> productDto.getName().toLowerCase().contains(category.toLowerCase()))
                    .filter(productDto -> productDto.getInventory().getAvailable() > 5)
                    .collect(Collectors.toList());

    }

    @Override
    public List<ProductDto> getProductsByCategoryAndSort(String category, SortOption sortBy) {
            List<ProductDto> productsByCategory = getProductsByCategory(category);

            switch (sortBy) {
                case INVENTORY:
                    return productsByCategory.stream()
                            .sorted(Comparator.comparingInt(p -> p.getInventory().getAvailable()))
                            .collect(Collectors.toList());


                case PRICE:
                    return productsByCategory.stream()
                            .sorted(Comparator.comparing(p -> p.getPrice().getAmount()))
                            .collect(Collectors.toList());

                default:
                    throw new IllegalArgumentException("Unsupported sort option:  " + sortBy);
            }
    }

    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getProductByIdFallbackMethod")
    @Override
    public ProductDto getProductById(Long productId) {
            ProductDto productDto = feignClient.getProductById(productId);
            if (productDto != null && productDto.getInventory().getAvailable() > 5) {
                return productDto;
            } else {
                return null;
            }
    }

    public List<ProductDto> getAllProductsFallbackMethod(Throwable throwable){
        // Create a ProductDto with "NAN" values
        ProductDto nanProduct = new ProductDto();
        nanProduct.setName("NAN");
        nanProduct.setBrand("NAN");
        nanProduct.setDescription("NAN");

        PriceDto nanPrice = new PriceDto();
        nanPrice.setCurrency("NAN");
        nanPrice.setAmount(BigDecimal.ZERO);
        nanProduct.setPrice(nanPrice);

        InventoryDto nanInventory = new InventoryDto();
        nanInventory.setTotal(0);
        nanInventory.setAvailable(0);
        nanInventory.setReserved(0);
        nanProduct.setInventory(nanInventory);

        AttributeDto nanAttribute = new AttributeDto();
        nanAttribute.setName("NAN");
        nanAttribute.setValue("NAN");
        nanProduct.setAttributes(Collections.singletonList(nanAttribute));

        return Collections.singletonList(nanProduct);
    }

    public ProductDto getProductByIdFallbackMethod(Long productId, Throwable throwable){
        List<ProductDto> fallbackProducts = getAllProductsFallbackMethod(throwable);
        if (!fallbackProducts.isEmpty()) {
            return fallbackProducts.get(0);
        } else {
            return null;
        }
    }
}
