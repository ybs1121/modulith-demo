package com.toy.modulithdemo.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;


    public Product create(String name, int stock) {
        return productRepository.save(new Product(name, stock));
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void decreaseStock(Long productId, int count) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        product.decreaseStock(count);
    }

    public void increaseStock(Long productId, int count) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        product.increaseStock(count);
    }
}