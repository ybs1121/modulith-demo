package com.toy.modulithdemo.order.port;

import java.math.BigDecimal;

public interface ProductPort {
    BigDecimal getProductPrice(Long productId);
}
