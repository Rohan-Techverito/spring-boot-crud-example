package com.javatechie.crud.example.service;

import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.exception.ProductNotFoundException;
import com.javatechie.crud.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void updateProduct_withExistingId_updatesAndReturnsProduct() {
        Product existingProduct = new Product(1, "Laptop", 5, 999.99);
        Product updatedRequest = new Product(1, "Laptop Pro", 10, 1299.99);

        when(repository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(repository.save(any(Product.class))).thenReturn(updatedRequest);

        Product result = service.updateProduct(updatedRequest);

        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getPrice()).isEqualTo(1299.99);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_withUnknownId_throwsProductNotFoundExceptionAndNeverSaves() {
        Product updateRequest = new Product(99, "Laptop", 5, 999.99);

        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.updateProduct(updateRequest));

        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_withExistingId_returnsProduct() {
        Product existingProduct = new Product(1, "Laptop", 5, 999.99);

        when(repository.findById(1)).thenReturn(Optional.of(existingProduct));

        Product result = service.getProductById(1);

        assertThat(result).isEqualTo(existingProduct);
    }

    @Test
    void getProductById_withUnknownId_throwsProductNotFoundException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.getProductById(99));
    }
}
