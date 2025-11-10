package com.gemini.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

    @Test
    public void testProduct() {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);

        assertEquals(1L, product.getId());
        assertEquals("Shirt", product.getName());
        assertEquals("A comfortable cotton shirt", product.getDescription());
        assertEquals(25.0, product.getPrice());
    }
}
