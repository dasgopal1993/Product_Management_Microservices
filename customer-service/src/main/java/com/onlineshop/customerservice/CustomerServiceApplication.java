package com.onlineshop.customerservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
		title = "Customer Service API",
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
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
