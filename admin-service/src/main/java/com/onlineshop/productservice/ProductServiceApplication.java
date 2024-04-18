package com.onlineshop.productservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


@OpenAPIDefinition(info = @Info(
		title = "Product Service API for Admin",
		description = "API for managing products in an online shop",
		version = "v1.0",
		contact = @Contact(
				name = "Gopal Das",
				email = "gopalassesment@gmail.com",
				url = "https://gopalassesment.com"
		)
))
@SpringBootApplication
@EnableFeignClients
public class ProductServiceApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
