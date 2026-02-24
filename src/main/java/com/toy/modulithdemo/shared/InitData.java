package com.toy.modulithdemo.shared;

import com.toy.modulithdemo.order.Order;
import com.toy.modulithdemo.order.OrderRepository;
import com.toy.modulithdemo.product.Product;
import com.toy.modulithdemo.product.ProductRepository;
import com.toy.modulithdemo.promotion.Promotion;
import com.toy.modulithdemo.promotion.PromotionRepository;
import com.toy.modulithdemo.user.User;
import com.toy.modulithdemo.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitData {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final StringRedisTemplate stringRedisTemplate;


    @PostConstruct
    public void init() {
        Product product = new Product("Phone", 1000, BigDecimal.valueOf(100_000));
        Product save = productRepository.save(product);

        log.info("Save Product : {}", save);

        Order order = new Order(save.getId(), 1, BigDecimal.valueOf(100_000), BigDecimal.valueOf(100_000), 1L);
        orderRepository.save(order);
        log.info("Save Order : {}", order);

        userRepository.save(User.create("test123", "qwer1234"));

        Promotion promotion = promotionRepository.save(Promotion.create("테스트", 100L, 100L));

        setInitialStock(promotion.getId(), promotion.getRemainQuantity());


    }

    public void setInitialStock(Long promotionId, long amount) {
        stringRedisTemplate.opsForValue().set("promotion:stock:" + promotionId, String.valueOf(amount));
        stringRedisTemplate.delete("promotion:users:" + promotionId); // 기존 유저 초기화
        stringRedisTemplate.delete("promotion:queue"); // 기존 큐 초기화
    }

}
