package com.toy.modulithdemo.product;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int stock; // 재고 수량

    private BigDecimal price;

    protected Product() {
    }

    public Product(String name, int stock, BigDecimal price) {
        this.name = name;
        this.stock = stock;
    }

    public void decreaseStock(int count) {
        this.stock -= count;
    }

    public void increaseStock(int count) {
        this.stock += count;
    }
}
