package com.toy.modulithdemo.order;

import jakarta.persistence .*;

@Entity
@Table(name = "orders") // order가 예약어일 수 있어 테이블명은 orders로
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int count;

    protected Order() {
    }

    public Order(Long productId, int count) {
        this.productId = productId;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public int getCount() {
        return count;
    }
}