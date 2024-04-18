package com.onlineshop.customerservice.service;

import com.onlineshop.customerservice.dto.AttributeDto;
import com.onlineshop.customerservice.dto.InventoryDto;
import com.onlineshop.customerservice.dto.PriceDto;
import com.onlineshop.customerservice.dto.ProductDto;
import com.onlineshop.customerservice.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerFeignClient feignClient;

    @InjectMocks
    private CustomerServiceImpl customerService;

    List<ProductDto> productDtos;

    ProductDto productDto;

    @BeforeEach
    public void setUp() {

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
    public void testGetAllProducts(){

        productDtos.get(0).setInventory(new InventoryDto(1L, 20, 4, 16));
        // Mock feignClient behavior
        doReturn(productDtos).when(feignClient).getAllProducts();

        // Test
        List<ProductDto> result = customerService.getAllProducts();

        // Verify
        assertEquals(1, result.size());
        assertEquals("Product 2", result.get(0).getName());

        assertTrue(result.stream().allMatch(productDto -> productDto.getInventory().getAvailable() > 5));

    }

    @Test
    public void testGetProductsByCategory(){

        productDtos.get(0).setName("jeans shirt");

        doReturn(productDtos).when(feignClient).getAllProducts();

        // Test
        String category = "shirt";
        List<ProductDto> result = customerService.getProductsByCategory(category);

        // Verify
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(productDto ->
                productDto.getInventory().getAvailable() > 5 && productDto.getName().toLowerCase().contains(category.toLowerCase())));

    }

    @Test
    public void testGetProductsByCategoryAndSort_SortsByINVENTORY() {

        // Mock feignClient behavior
        doReturn(productDtos).when(feignClient).getAllProducts();

        String category = "product";
        // Test
        List<ProductDto> result = customerService.getProductsByCategoryAndSort(category, SortOption.INVENTORY);

        // Verify
        assertEquals(2, result.size());

        // Ensure the sorting order is correct based on inventory availability
        assertTrue(result.get(0).getInventory().getAvailable() <= result.get(1).getInventory().getAvailable(),
                "Products are not sorted correctly by inventory availability");
    }

    @Test
    public void testGetProductsByCategoryAndSort_SortsByPRICE() {

        // Mock feignClient behavior
        doReturn(productDtos).when(feignClient).getAllProducts();

        String category = "product";
        // Test
        List<ProductDto> result = customerService.getProductsByCategoryAndSort(category, SortOption.PRICE);

        // Verify
        assertEquals(2, result.size());

        // Ensure the sorting order is correct based on price
        assertTrue(result.get(1).getPrice().getAmount().compareTo(result.get(0).getPrice().getAmount()) >= 0,
                "Products are not sorted correctly by price");
    }

    @Test
    public void testGetProductsByCategoryAndSort_ThrowsExceptionForUnsupportedSortOption() {

        doReturn(productDtos).when(feignClient).getAllProducts();

        String category = "product";
        SortOption unsupportedSortOption = SortOption.UNKNOWN;

        Executable executable = () -> customerService.getProductsByCategoryAndSort(category, unsupportedSortOption);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Unsupported sort option:  " + unsupportedSortOption, exception.getMessage());
    }

    @Test
    public void testGetProductById_ReturnsProductDto() {

        Long productId = 123L;
        ProductDto expectedProductDto = productDto;

        doReturn(expectedProductDto).when(feignClient).getProductById(productId);

        ProductDto actualProductDto = customerService.getProductById(productId);

        assertEquals(expectedProductDto, actualProductDto, "Returned ProductDto should match the expected one");
    }

    @Test
    public void testGetProductById_ReturnsNullWhenProductNotAvailable() {

        Long productId = 123L;

        doReturn(null).when(feignClient).getProductById(productId);

        ProductDto actualProductDto = customerService.getProductById(productId);

        assertNull(actualProductDto, "Returned ProductDto should be null when product is not available");
    }

    @Test
    public void testGetAllProductsFallbackMethod_ReturnsFallbackProductDto() {

        Throwable throwable = new RuntimeException("Simulated Feign client failure");

        List<ProductDto> fallbackProducts = customerService.getAllProductsFallbackMethod(throwable);

        assertNotNull(fallbackProducts, "Fallback products should not be null");
        assertEquals(1, fallbackProducts.size(), "Fallback products list should contain exactly one product");

        ProductDto fallbackProduct = fallbackProducts.get(0);
        assertEquals("NAN", fallbackProduct.getName(), "Fallback product name should be 'NAN'");
        assertEquals("NAN", fallbackProduct.getBrand(), "Fallback product brand should be 'NAN'");
        assertEquals("NAN", fallbackProduct.getDescription(), "Fallback product description should be 'NAN'");

        PriceDto fallbackPrice = fallbackProduct.getPrice();
        assertNotNull(fallbackPrice, "Fallback product price should not be null");
        assertEquals("NAN", fallbackPrice.getCurrency(), "Fallback product currency should be 'NAN'");
        assertEquals(BigDecimal.ZERO, fallbackPrice.getAmount(), "Fallback product amount should be zero");

        InventoryDto fallbackInventory = fallbackProduct.getInventory();
        assertNotNull(fallbackInventory, "Fallback product inventory should not be null");
        assertEquals(0, fallbackInventory.getTotal(), "Fallback product total inventory should be zero");
        assertEquals(0, fallbackInventory.getAvailable(), "Fallback product available inventory should be zero");
        assertEquals(0, fallbackInventory.getReserved(), "Fallback product reserved inventory should be zero");

        List<AttributeDto> fallbackAttributes = fallbackProduct.getAttributes();
        assertNotNull(fallbackAttributes, "Fallback product attributes should not be null");
        assertEquals(1, fallbackAttributes.size(), "Fallback product attributes list should contain exactly one attribute");

        AttributeDto fallbackAttribute = fallbackAttributes.get(0);
        assertEquals("NAN", fallbackAttribute.getName(), "Fallback attribute name should be 'NAN'");
        assertEquals("NAN", fallbackAttribute.getValue(), "Fallback attribute value should be 'NAN'");
    }

    @Test
    void testGetProductByIdFallbackMethod_ReturnsFallbackProduct() {

        Long productId = 123L;
        Throwable throwable = new RuntimeException("Simulated Feign client failure");

        ProductDto result = customerService.getProductByIdFallbackMethod(productId, throwable);

        assertEquals(createFallbackProduct().getProductId(), result.getProductId());
    }

    @Test
    void testGetProductByIdFallbackMethod_ReturnsNullWhenNoFallbackProducts() {

        Long productId = 123L;
        Throwable throwable = new RuntimeException("Simulated Feign client failure");


        ProductDto result = customerService.getProductByIdFallbackMethod(productId, throwable);

        assertNull(result.getProductId());
    }

    private ProductDto createFallbackProduct() {
        ProductDto productDto = new ProductDto();
        return productDto;
    }
}
