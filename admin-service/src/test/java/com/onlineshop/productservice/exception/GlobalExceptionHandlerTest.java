package com.onlineshop.productservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testHandleGlobalException() {
        // Mock exception and web request
        Exception exception = new Exception("something is wrong");
        when(webRequest.getDescription(false)).thenReturn("Test request description");

        // Call the handleGlobalException method
        ResponseEntity<ErrorDetails> responseEntity = globalExceptionHandler.handleGlobalException(exception, webRequest);

        // Verify the response entity
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorDetails errorDetails = responseEntity.getBody();
        assertEquals("something is wrong", errorDetails.getMessage());
        assertEquals("INTERNAL SERVER ERROR", errorDetails.getErrorCode());
        assertEquals("Test request description", errorDetails.getPath());
    }
}
