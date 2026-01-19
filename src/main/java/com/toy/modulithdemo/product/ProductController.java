package com.toy.modulithdemo.product;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Product create(@RequestParam String name,
                          @RequestParam int stock) {
        return productService.create(name, stock);
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }
}