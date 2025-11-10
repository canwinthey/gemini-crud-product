package com.gemini.product.mapper;

import com.gemini.product.domain.ProductDto;
import com.gemini.product.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper();

    @Test
    public void testToDto() {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        ProductDto productDto = productMapper.toDto(product);

        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(product.getDescription(), productDto.getDescription());
        assertEquals(product.getPrice(), productDto.getPrice());
    }

    @Test
    public void testToDto_null() {
        assertNull(productMapper.toDto(null));
    }

    @Test
    public void testToEntity() {
        ProductDto productDto = new ProductDto(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        Product product = productMapper.toEntity(productDto);

        assertEquals(productDto.getId(), product.getId());
        assertEquals(productDto.getName(), product.getName());
        assertEquals(productDto.getDescription(), product.getDescription());
        assertEquals(productDto.getPrice(), product.getPrice());
    }

    @Test
    public void testToEntity_null() {
        assertNull(productMapper.toEntity(null));
    }
}
