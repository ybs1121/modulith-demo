package com.toy.modulithdemo.shared.event;


import lombok.Data;

import java.io.Serializable;

@Data
public class ProductUsedEvent implements Serializable {

    private final Long productId;
    private final int count;

    public ProductUsedEvent(Long productId, int count) {
        this.productId = productId;
        this.count = count;
    }

}