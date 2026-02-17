package com.toy.modulithdemo.view;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/products/{productId}/view")
    public String videoPage(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "/video-view"; // resources/templates/video-view.html
    }
}