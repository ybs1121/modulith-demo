package com.toy.modulithdemo.product;


public class ProductUsedEvent {

    private final Long productId;
    private final int count;

    public ProductUsedEvent(Long productId, int count) {
        this.productId = productId;
        this.count = count;
    }

    public Long getProductId() {
        return productId;
    }

    public int getCount() {
        return count;
    }
}