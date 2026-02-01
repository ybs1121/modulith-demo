package com.toy.modulithdemo.product;

import com.toy.modulithdemo.order.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductAdapter implements ProductPort {

    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public BigDecimal getProductPrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        return product.getPrice();
    }

}