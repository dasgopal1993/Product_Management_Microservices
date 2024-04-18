package com.onlineshop.productservice.service.impl;

import com.onlineshop.productservice.dto.AttributeDto;
import com.onlineshop.productservice.dto.InventoryDto;
import com.onlineshop.productservice.dto.PriceDto;
import com.onlineshop.productservice.dto.ProductDto;
import com.onlineshop.productservice.entity.Attribute;
import com.onlineshop.productservice.entity.Inventory;
import com.onlineshop.productservice.entity.Price;
import com.onlineshop.productservice.entity.Product;
import com.onlineshop.productservice.exception.ProductNotFoundException;
import com.onlineshop.productservice.exception.ProductServiceException;
import com.onlineshop.productservice.repository.ProductRepository;
import com.onlineshop.productservice.service.AdminFeignClient;
import com.onlineshop.productservice.service.ProductService;
import com.onlineshop.productservice.service.SortOption;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private ModelMapper modelMapper;

    private AdminFeignClient adminFeignClient;




    @Override
    public List<ProductDto> createProducts(List<ProductDto> productDto) {
        try {
            List<Product> products = productDto.stream().map(singleProductDto -> modelMapper.map(singleProductDto, Product.class)).collect(Collectors.toList());
            List<Product> savedProducts = productRepository.saveAll(products);
            List<ProductDto> savedProductDtos = savedProducts.stream().map(savedProduct -> modelMapper.map(savedProduct, ProductDto.class)).collect(Collectors.toList());
            return savedProductDtos;
        } catch (Exception e) {
            throw new ProductServiceException("Failed to create products", e);
        }
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return modelMapper.map(product, ProductDto.class);
        } else {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
    }

    @Override
    public List<ProductDto> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return products.stream().map(product -> modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ProductServiceException("Failed to retrieve products", e);
        }
    }

    public List<ProductDto> getAllInventoryLimitedOrUnavailable() {
            List<Product> products = productRepository.findAll();
            return products.stream()
                    .filter(product -> {
                        int availableInventory = product.getInventory().getAvailable();
                        return availableInventory == 0 || availableInventory <= 5;
                    })
                    .map(product -> modelMapper.map(product, ProductDto.class))
                    .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getProductsByCategoryfallbackMethod")
    @Override
    public List<ProductDto> getProductsByCategory(String category) {
        return adminFeignClient.getProductsByCategory(category);
    }

    @CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getProductsByCategoryAndSortfallbackMethod")
    @Override
    public List<ProductDto> getProductsByCategoryAndSort(String category, SortOption sortOption) {
        return adminFeignClient.getProductsByCategoryAndSort(category, sortOption);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
            Product existingProduct = productRepository.findById(productDto.getProductId()).get();
            existingProduct.setName(productDto.getName());
            existingProduct.setBrand(productDto.getBrand());
            existingProduct.setDescription(productDto.getDescription());
            // Update Price
            Price price = new Price();
            price.setCurrency(productDto.getPrice().getCurrency());
            price.setAmount(productDto.getPrice().getAmount());
            //Set Price in existingProduct
            existingProduct.setPrice(price);

            //Update Inventory
            Inventory inventory = new Inventory();
            inventory.setTotal(productDto.getInventory().getTotal());
            inventory.setAvailable(productDto.getInventory().getAvailable());
            inventory.setReserved(productDto.getInventory().getReserved());
            //Set Inventory in existingProduct
            existingProduct.setInventory(inventory);

            //Update Attributes
            List<AttributeDto> attributeDtos = productDto.getAttributes();
            List<Attribute> attributes = new ArrayList<>();
            for (AttributeDto attributeDto : attributeDtos) {
                Attribute attribute = new Attribute();
                attribute.setName(attributeDto.getName());
                attribute.setValue(attributeDto.getValue());
                attributes.add(attribute);
            }
            //Set Attributes in existingProduct
            existingProduct.setAttributes(attributes);
            //Saved updated product in database
            Product savedProduct = productRepository.save(existingProduct);
            ProductDto savedProductDto = modelMapper.map(savedProduct, ProductDto.class);

            return savedProductDto;
    }

    @Override
    public void deleteProduct(Long productId) {

            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (!optionalProduct.isPresent()) {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
            productRepository.deleteById(productId);

    }

    public List<ProductDto> getProductsByCategoryfallbackMethod(String category, Throwable throwable) {
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

    public List<ProductDto> getProductsByCategoryAndSortfallbackMethod(String category, SortOption sortOption, Throwable throwable) {

        return getProductsByCategoryfallbackMethod(category, throwable);
    }
}
