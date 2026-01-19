package com.toy.modulithdemo.product;


import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductEventHandler {

    private final ProductService productService;

    public ProductEventHandler(ProductService productService) {
        this.productService = productService;
    }

    @EventListener
    @Transactional
    public void on(ProductUsedEvent event) {
        productService.decreaseStock(event.getProductId(), event.getCount());
    }
}