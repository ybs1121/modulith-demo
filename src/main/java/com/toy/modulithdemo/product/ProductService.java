package com.toy.modulithdemo.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    private final Semaphore limiter = new Semaphore(50);


    public Product create(String name, int stock, BigDecimal price) {
        return productRepository.save(new Product(name, stock, price));
    }

//    @Transactional(readOnly = true)
//    public List<Product> findAll() {

    /// /        log.info("in");
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            log.error("--");
//        }
//
//        return productRepository.findAll();
//    }
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        try {
            // 1. 입장권 확인 (3초만 기다려보고 안 되면 포기)
            if (!limiter.tryAcquire(3, TimeUnit.SECONDS)) {
                // 줄도 못 서보고 바로 거절당함 (Fail Fast) -> 시스템 보호
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "접속량 폭주");
            }

            // 2. 실제 로직 (DB 조회 흉내 1초)
            Thread.sleep(1000);

        } catch (Exception e) {
            log.info("e");
        } finally {
            // 3. 퇴장 시 반드시 반납
            limiter.release();
        }

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