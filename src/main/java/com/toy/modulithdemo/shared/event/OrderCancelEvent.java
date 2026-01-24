package com.toy.modulithdemo.shared.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class OrderCancelEvent implements Serializable {
    private final Long productId;
    private final int count;

}
