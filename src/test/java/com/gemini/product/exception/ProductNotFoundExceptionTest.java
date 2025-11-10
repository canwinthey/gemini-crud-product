package com.gemini.product.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductNotFoundExceptionTest {

    @Test
    public void testProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");
        assertEquals("Product not found", ex.getMessage());
    }
}
