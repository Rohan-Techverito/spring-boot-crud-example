package com.javatechie.crud.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.exception.ProductNotFoundException;
import com.javatechie.crud.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addProduct_withInvalidPayload_returns400WithErrors() throws Exception {
        Product invalidProduct = new Product(0, "", -1, -10.0);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void addProduct_withValidPayload_returns201AndSavedProduct() throws Exception {
        Product validProduct = new Product(1, "Laptop", 5, 999.99);

        when(service.saveProduct(any(Product.class))).thenReturn(validProduct);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validProduct.getId()))
                .andExpect(jsonPath("$.name").value(validProduct.getName()))
                .andExpect(jsonPath("$.quantity").value(validProduct.getQuantity()))
                .andExpect(jsonPath("$.price").value(validProduct.getPrice()));

        verify(service, times(1)).saveProduct(any(Product.class));
    }

    @Test
    void updateProduct_withValidId_returns200AndUpdatedProduct() throws Exception {
        Product updatedProduct = new Product(1, "Laptop Pro", 10, 1299.99);

        when(service.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedProduct.getId()))
                .andExpect(jsonPath("$.name").value(updatedProduct.getName()))
                .andExpect(jsonPath("$.quantity").value(updatedProduct.getQuantity()))
                .andExpect(jsonPath("$.price").value(updatedProduct.getPrice()));

        verify(service, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void updateProduct_withUnknownId_returns404WithMessage() throws Exception {
        Product unknownProduct = new Product(99, "Laptop", 5, 999.99);

        when(service.updateProduct(any(Product.class))).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unknownProduct)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void findProductById_withUnknownId_returns404() throws Exception {
        when(service.getProductById(anyInt())).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(get("/productById/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
