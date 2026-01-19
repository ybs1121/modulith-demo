package com.toy.modulithdemo.order;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order create(@RequestParam Long productId,
                        @RequestParam int count) {
        return orderService.create(productId, count);
    }
}