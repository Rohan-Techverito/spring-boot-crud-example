package com.javatechie.crud.example.service;

import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.exception.ProductNotFoundException;
import com.javatechie.crud.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void getProductById_shouldThrowWhenNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.getProductById(1));
    }

    @Test
    void getProductByName_shouldThrowWhenNotFound() {
        when(repository.findByName("unknown")).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> service.getProductByName("unknown"));
    }

    @Test
    void updateProduct_shouldThrowWhenNotFound() {
        Product product = new Product(1, "name", 5, 10.0);
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.updateProduct(product));
    }

    @Test
    void deleteProduct_shouldThrowWhenNotFound() {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(1);

        assertThrows(ProductNotFoundException.class, () -> service.deleteProduct(1));
    }

    @Test
    void getProducts_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> page = new PageImpl<>(Collections.singletonList(new Product(1, "name", 5, 10.0)));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<Product> result = service.getProducts(pageable);

        assertEquals(1, result.getTotalElements());
    }
}
