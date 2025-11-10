package com.gemini.product.service;

import com.gemini.product.model.Product;
import com.gemini.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void testGetAllProducts() {
        Product product1 = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        Product product2 = new Product(2L, "Trouser", "A pair of stylish trousers", 45.0);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        assertEquals(2, productService.getAllProducts().size());
    }

    @Test
    public void testGetProductById() {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals("Shirt", productService.getProductById(1L).get().getName());
    }

    @Test
    public void testCreateProduct() {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertEquals("Shirt", productService.createProduct(product).getName());
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        Product updatedProduct = new Product(1L, "Shirt", "A comfortable cotton shirt", 30.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        assertEquals(30.0, productService.updateProduct(1L, updatedProduct).getPrice());
    }

    @Test
    public void testDeleteProduct() {
        productService.deleteProduct(1L);
    }
}
