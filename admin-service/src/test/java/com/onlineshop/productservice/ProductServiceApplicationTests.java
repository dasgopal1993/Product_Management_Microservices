package com.onlineshop.productservice;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {

	}

	@Test
	public void testMainMethod() {
		// Call the main method of your Spring Boot application class
		SpringApplication.run(ProductServiceApplication.class, new String[] {});
	}

}
