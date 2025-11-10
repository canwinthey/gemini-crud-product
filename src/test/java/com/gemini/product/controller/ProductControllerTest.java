package com.gemini.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.product.domain.ProductDto;
import com.gemini.product.mapper.ProductMapper;
import com.gemini.product.model.Product;
import com.gemini.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testGetAllProducts() throws Exception {
        Product product1 = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        Product product2 = new Product(2L, "Trouser", "A pair of stylish trousers", 45.0);
        ProductDto productDto1 = new ProductDto(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        ProductDto productDto2 = new ProductDto(2L, "Trouser", "A pair of stylish trousers", 45.0);

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));
        when(productMapper.toDto(product1)).thenReturn(productDto1);
        when(productMapper.toDto(product2)).thenReturn(productDto2);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Shirt"))
                .andExpect(jsonPath("$[1].name").value("Trouser"));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 25.0);
        ProductDto productDto = new ProductDto(1L, "Shirt", "A comfortable cotton shirt", 25.0);

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Shirt"));
    }

    @Test
    public void testCreateProduct() throws Exception {
        ProductDto productDto = new ProductDto(null, "Shoes", "A pair of leather shoes", 75.0);
        Product product = new Product(3L, "Shoes", "A pair of leather shoes", 75.0);
        ProductDto createdProductDto = new ProductDto(3L, "Shoes", "A pair of leather shoes", 75.0);

        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(product);
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(createdProductDto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Shoes"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        ProductDto productDto = new ProductDto(1L, "Shirt", "A comfortable cotton shirt", 30.0);
        Product product = new Product(1L, "Shirt", "A comfortable cotton shirt", 30.0);
        ProductDto updatedProductDto = new ProductDto(1L, "Shirt", "A comfortable cotton shirt", 30.0);

        when(productMapper.toEntity(any(ProductDto.class))).thenReturn(product);
        when(productService.updateProduct(any(Long.class), any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(updatedProductDto);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(30.0));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
