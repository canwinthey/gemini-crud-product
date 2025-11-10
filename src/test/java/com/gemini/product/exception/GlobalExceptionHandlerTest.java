package com.gemini.product.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleProductNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Product not found", ((Map<String, Object>) responseEntity.getBody()).get("message"));
    }

    @Test
    public void testHandleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("productDto", "name", "Name is mandatory")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(ex);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Name is mandatory", ((java.util.List<String>) ((Map<String, Object>) responseEntity.getBody()).get("errors")).get(0));
    }
}
