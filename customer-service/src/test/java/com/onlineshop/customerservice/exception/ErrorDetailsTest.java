package com.onlineshop.customerservice.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorDetailsTest {

    @Test
    public void testErrorDetailsConstructorAndGetters() {
        // Create a LocalDateTime instance
        LocalDateTime timestamp = LocalDateTime.now();

        // Create an ErrorDetails instance using the constructor
        ErrorDetails errorDetails = new ErrorDetails(timestamp, "Test message", "/test/path", "ERROR_CODE");

        // Verify the values returned by the getters
        assertEquals(timestamp, errorDetails.getTimestamp());
        assertEquals("Test message", errorDetails.getMessage());
        assertEquals("/test/path", errorDetails.getPath());
        assertEquals("ERROR_CODE", errorDetails.getErrorCode());
    }

    @Test
    public void testErrorDetailsSetters() {
        // Create an ErrorDetails instance
        ErrorDetails errorDetails = new ErrorDetails();

        // Set values using setters
        LocalDateTime timestamp = LocalDateTime.now();
        errorDetails.setTimestamp(timestamp);
        errorDetails.setMessage("Test message");
        errorDetails.setPath("/test/path");
        errorDetails.setErrorCode("ERROR_CODE");

        // Verify the values returned by the getters
        assertEquals(timestamp, errorDetails.getTimestamp());
        assertEquals("Test message", errorDetails.getMessage());
        assertEquals("/test/path", errorDetails.getPath());
        assertEquals("ERROR_CODE", errorDetails.getErrorCode());
    }
}
