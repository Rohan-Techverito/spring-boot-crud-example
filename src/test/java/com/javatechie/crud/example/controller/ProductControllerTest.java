package com.javatechie.crud.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.exception.ProductNotFoundException;
import com.javatechie.crud.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAllProducts_shouldUseDefaultPagination() throws Exception {
        Page<Product> page = new PageImpl<>(Collections.singletonList(new Product(1, "name", 5, 10.0)));
        when(service.getProducts(eq(PageRequest.of(0, 20)))).thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).getProducts(eq(PageRequest.of(0, 20)));
    }

    @Test
    void findAllProducts_shouldUseCustomPagination() throws Exception {
        Page<Product> page = new PageImpl<>(Collections.singletonList(new Product(1, "name", 5, 10.0)));
        when(service.getProducts(eq(PageRequest.of(1, 5)))).thenReturn(page);

        mockMvc.perform(get("/products").param("page", "1").param("size", "5"))
                .andExpect(status().isOk());

        verify(service).getProducts(eq(PageRequest.of(1, 5)));
    }

    @Test
    void findProductById_shouldReturn404WhenNotFound() throws Exception {
        when(service.getProductById(anyInt())).thenThrow(new ProductNotFoundException("Product not found with id: 1"));

        mockMvc.perform(get("/productById/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 1"));
    }

    @Test
    void findProductByName_shouldReturn404WhenNotFound() throws Exception {
        when(service.getProductByName("unknown")).thenThrow(new ProductNotFoundException("Product not found with name: unknown"));

        mockMvc.perform(get("/product/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with name: unknown"));
    }

    @Test
    void updateProduct_shouldReturn404WhenNotFound() throws Exception {
        Product product = new Product(1, "name", 5, 10.0);
        when(service.updateProduct(any(Product.class))).thenThrow(new ProductNotFoundException("Product not found with id: 1"));

        mockMvc.perform(put("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 1"));
    }

    @Test
    void deleteProduct_shouldReturn404WhenNotFound() throws Exception {
        when(service.deleteProduct(anyInt())).thenThrow(new ProductNotFoundException("Product not found with id: 1"));

        mockMvc.perform(delete("/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 1"));
    }
}
