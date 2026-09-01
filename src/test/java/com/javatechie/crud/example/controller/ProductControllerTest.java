package com.javatechie.crud.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.exception.GlobalExceptionHandler;
import com.javatechie.crud.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void addProduct_withBlankName_returns400() throws Exception {
        Product product = new Product(0, "", 10, 100.0);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void addProduct_withNegativeQuantity_returns400() throws Exception {
        Product product = new Product(0, "Laptop", -1, 100.0);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void addProduct_withNegativePrice_returns400() throws Exception {
        Product product = new Product(0, "Laptop", 10, -100.0);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void addProduct_withValidProduct_returnsSavedProduct() throws Exception {
        Product product = new Product(0, "Laptop", 10, 999.99);

        when(productService.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.price").value(999.99));
    }
}
