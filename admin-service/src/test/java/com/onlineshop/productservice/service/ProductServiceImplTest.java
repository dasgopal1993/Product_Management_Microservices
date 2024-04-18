package com.onlineshop.productservice.service;

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
import com.onlineshop.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AdminFeignClient adminFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @InjectMocks
    ProductServiceImpl productService;

    List<ProductDto> productDtos;
    List<Product> products;

    Product product;
    ProductDto productDto;

    @BeforeEach
    public void setUp() {

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
    public void CreateProductsTest() {

        List<ProductDto> expectedProductDtos = productDtos;

        when(modelMapper.map(any(ProductDto.class), eq(Product.class))).thenAnswer(invocation -> {
            ProductDto dto = invocation.getArgument(0);
            return new Product(dto.getProductId(), dto.getName(), dto.getBrand(), dto.getDescription(),
                    dto.getPrice() != null ? new Price(dto.getPrice().getPriceId(), dto.getPrice().getCurrency(), dto.getPrice().getAmount()) : null,
                    dto.getInventory() != null ? new Inventory(dto.getInventory().getInventoryId(), dto.getInventory().getTotal(), dto.getInventory().getAvailable(), dto.getInventory().getReserved()) : null,
                    dto.getAttributes() != null ? dto.getAttributes().stream().map(attr -> new Attribute(attr.getAttributeId(), attr.getName(), attr.getValue())).collect(Collectors.toList()) : null);
        });

        when(productRepository.saveAll(products)).thenReturn(products);

        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return new ProductDto(product.getProductId(), product.getName(), product.getBrand(), product.getDescription(),
                    product.getPrice() != null ? new PriceDto(product.getPrice().getPriceId(), product.getPrice().getCurrency(), product.getPrice().getAmount()) : null,
                    product.getInventory() != null ? new InventoryDto(product.getInventory().getInventoryId(), product.getInventory().getTotal(), product.getInventory().getAvailable(), product.getInventory().getReserved()) : null,
                    product.getAttributes() != null ? product.getAttributes().stream().map(attr -> new AttributeDto(attr.getAttributeId(), attr.getName(), attr.getValue())).collect(Collectors.toList()) : null);
        });

        // When
        List<ProductDto> savedProductDtos = productService.createProducts(productDtos);

        // Then
        assertNotNull(savedProductDtos);
        assertEquals(expectedProductDtos.get(0).getProductId(), savedProductDtos.get(0).getProductId());
    }

    @Test
    public void CreateProductsExceptionTest() {

        when(productRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(ProductServiceException.class, () -> productService.createProducts(productDtos));
    }

    @Test
    public void testGetProductById_ExistingProduct() {

        ProductDto expectedProductDto = productDto;

        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        when(modelMapper.map(product, ProductDto.class)).thenReturn(expectedProductDto);

        ProductDto result = productService.getProductById(product.getProductId());

        assertNotNull(result);
        assertEquals(expectedProductDto, result);

    }

    @Test
    public void testGetProductById_NonExistingProduct() {

        Long productId = 2L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    public void testGetAllProducts_Success() {

        List<ProductDto> expectedProductDtos = productDtos;

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(products.get(0), ProductDto.class)).thenReturn(expectedProductDtos.get(0));
        when(modelMapper.map(products.get(1), ProductDto.class)).thenReturn(expectedProductDtos.get(1));

        List<ProductDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(expectedProductDtos.size(), result.size());
        assertEquals(expectedProductDtos.get(0).getProductId(), result.get(0).getProductId());
        assertEquals(expectedProductDtos.get(1).getProductId(), result.get(1).getProductId());
        assertEquals(expectedProductDtos.size(), result.size());

    }

    @Test
    public void testGetAllProducts_EmptyList() {

        List<Product> products = new ArrayList<>();
        List<ProductDto> expectedProductDtos = new ArrayList<>();

        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(expectedProductDtos.size(), result.size());

    }

    @Test
    public void testGetAllProducts_Exception() {

        when(productRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(ProductServiceException.class, () -> productService.getAllProducts());
    }

    @Test
    public void testGetAllInventoryLimitedOrUnavailable() {

        List<Product> productList = products;
        productList.get(0).setInventory(new Inventory(1L, 10, 4, 6));
        productList.get(1).setInventory(new Inventory(1L, 10, 0, 10));

        List<ProductDto> expectedProductDtos = productDtos;
        expectedProductDtos.get(0).setInventory(new InventoryDto(1L, 10, 4, 6));
        expectedProductDtos.get(1).setInventory(new InventoryDto(1L, 10, 0, 10));

        when(productRepository.findAll()).thenReturn(products);

        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return new ProductDto(product.getProductId(), product.getName(), product.getBrand(), product.getDescription(),
                    product.getPrice() != null ? new PriceDto(product.getPrice().getPriceId(), product.getPrice().getCurrency(), product.getPrice().getAmount()) : null,
                    product.getInventory() != null ? new InventoryDto(product.getInventory().getInventoryId(), product.getInventory().getTotal(), product.getInventory().getAvailable(), product.getInventory().getReserved()) : null,
                    product.getAttributes() != null ? product.getAttributes().stream().map(attr -> new AttributeDto(attr.getAttributeId(), attr.getName(), attr.getValue())).collect(Collectors.toList()) : null);
        });

        List<ProductDto> result = productService.getAllInventoryLimitedOrUnavailable();

//       If product is not greater than 5 then testGetAllInventory is Limited
        assertFalse(result.get(0).getInventory().getAvailable() > 5);

//        If product is equal to zero then testGetAllInventory is unavailable
        assertEquals(expectedProductDtos.get(1).getInventory().getAvailable(), 0);

    }

    @Test
    public void testGetProductsByCategorySuccess() {

        //Mock Feign client response
        when(adminFeignClient.getProductsByCategory("category")).thenReturn(Collections.singletonList(new ProductDto()));

        //call the method under test
        List<ProductDto> productResult = productService.getProductsByCategory("category");

        //Verify that the Feign client method was called with the correct argument
        verify(adminFeignClient).getProductsByCategory("category");

        //Verify that the returned list is not null and contains the expected product
        assertNotNull(productResult);
        assertFalse(productResult.isEmpty());

    }

    @Test
    public void testGetProductsByCategoryFallBack() {

        // Mock Feign client response to simulate a failure
        when(adminFeignClient.getProductsByCategory("category")).thenThrow(new RuntimeException());

        // Call the method under test and capture any exception
        List<ProductDto> productResult = null;

        try {
            productResult = productService.getProductsByCategory("category");

        } catch (RuntimeException e) {

        }
        // Verify that the Feign client method was called with the correct argument
        verify(adminFeignClient).getProductsByCategory("category");

        // Verify that the returned list is null or empty due to fallback
        assertNull(productResult);
    }

    @ParameterizedTest
    @EnumSource(SortOption.class)
    public void testGetProductsByCategoryAndSortSuccess(SortOption sortOption) {

        // Mock successful response from Feign client
        List<ProductDto> expectedProducts = Collections.singletonList(new ProductDto());

        when(adminFeignClient.getProductsByCategoryAndSort("category", sortOption)).thenReturn(expectedProducts);

        // Call the method under test
        List<ProductDto> sortedProducts = productService.getProductsByCategoryAndSort("category", sortOption);

        // Verify that the Feign client method was called with the correct arguments
        verify(adminFeignClient).getProductsByCategoryAndSort("category", sortOption);

        assertNotNull(sortedProducts);

    }

    @Test
    public void testUpdateProduct() {
        // Prepare test data
        ProductDto updateProductDto = productDto;
        updateProductDto.setProductId(1L);
        updateProductDto.setName("Updated Name");
        updateProductDto.setBrand("Updated Brand");
        updateProductDto.setDescription("Updated Description");

        PriceDto priceDto = new PriceDto();
        priceDto.setCurrency("USD");
        priceDto.setAmount(new BigDecimal("99.99"));
        updateProductDto.setPrice(priceDto);

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setTotal(100);
        inventoryDto.setAvailable(80);
        inventoryDto.setReserved(20);
        updateProductDto.setInventory(inventoryDto);

        List<AttributeDto> attributeDtos = new ArrayList<>();
        AttributeDto attributeDto = new AttributeDto();
        attributeDto.setName("Color");
        attributeDto.setValue("Blue");
        attributeDtos.add(attributeDto);
        updateProductDto.setAttributes(attributeDtos);

        // Mock repository behavior
        Product existingProduct = product; // Create an existing product
        given(productRepository.findById(productDto.getProductId())).willReturn(Optional.of(existingProduct));
        given(productRepository.save(any(Product.class))).willReturn(existingProduct);
        given(modelMapper.map(any(), any())).willReturn(productDto);

        // When
        ProductDto updatedProductDto = productService.updateProduct(updateProductDto);

        // Then
        assertNotNull(updatedProductDto);

        // Verify repository method calls
        verify(productRepository).findById(productDto.getProductId());
        verify(productRepository).save(any(Product.class));

        // Verify mapper method call
        verify(modelMapper).map(existingProduct, ProductDto.class);

        // Assert that all fields are updated correctly
        assertEquals(updatedProductDto.getName(), "Updated Name");
        assertEquals(updatedProductDto.getBrand(), "Updated Brand");
        assertEquals(updatedProductDto.getDescription(), "Updated Description");
        assertEquals(updatedProductDto.getPrice().getCurrency(), "USD");
        assertEquals(updatedProductDto.getPrice().getAmount(), new BigDecimal("99.99"));
        assertEquals(updatedProductDto.getInventory().getTotal(), 100);
        assertEquals(updatedProductDto.getInventory().getAvailable(), 80);
        assertEquals(updatedProductDto.getInventory().getReserved(), 20);
        assertEquals(updatedProductDto.getAttributes().size(), 1);
        assertEquals(updatedProductDto.getAttributes().get(0).getName(), "Color");
        assertEquals(updatedProductDto.getAttributes().get(0).getValue(), "Blue");
    }

    @Test
    public void testDeleteProduct() {
        // Mock repository behavior
        Long productId = 1L;

        // Mock product existence
        Product existingProduct = product;
        existingProduct.setProductId(productId);
        Optional<Product> optionalProduct = Optional.of(existingProduct);
        when(productRepository.findById(productId)).thenReturn(optionalProduct);

        // Delete product
        productService.deleteProduct(productId);

        // Verify repository method calls
        verify(productRepository).findById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    public void testDeleteProduct_ProductNotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    public void testGetProductsByCategoryfallbackMethod() {

        // Call the fallback method
        String category = "Test Category";
        Throwable throwable = new RuntimeException("Simulated exception");
        List<ProductDto> fallbackResult = productService.getProductsByCategoryfallbackMethod(category, throwable);

        // Verify the result
        assertEquals(1, fallbackResult.size());

        ProductDto nanProduct = fallbackResult.get(0);
        assertEquals("NAN", nanProduct.getName());
        assertEquals("NAN", nanProduct.getBrand());
        assertEquals("NAN", nanProduct.getDescription());

        PriceDto nanPrice = nanProduct.getPrice();
        assertEquals("NAN", nanPrice.getCurrency());
        assertEquals(BigDecimal.ZERO, nanPrice.getAmount());

        InventoryDto nanInventory = nanProduct.getInventory();
        assertEquals(0, nanInventory.getTotal());
        assertEquals(0, nanInventory.getAvailable());
        assertEquals(0, nanInventory.getReserved());

        List<AttributeDto> attributes = nanProduct.getAttributes();
        assertEquals(1, attributes.size());
        AttributeDto nanAttribute = attributes.get(0);
        assertEquals("NAN", nanAttribute.getName());
        assertEquals("NAN", nanAttribute.getValue());
    }

    @ParameterizedTest
    @EnumSource(SortOption.class)
    public void testGetProductsByCategoryAndSortfallbackMethod(SortOption sortOption) {

        // Call the fallback method
        String category = "Test Category";
        Throwable throwable = new RuntimeException("Simulated exception");
        List<ProductDto> fallbackResult = productService.getProductsByCategoryAndSortfallbackMethod(category, sortOption, throwable);

        // Verify the result
        assertEquals(1, fallbackResult.size());

        ProductDto nanProduct = fallbackResult.get(0);
        assertEquals("NAN", nanProduct.getName());
        assertEquals("NAN", nanProduct.getBrand());
        assertEquals("NAN", nanProduct.getDescription());
    }

}
