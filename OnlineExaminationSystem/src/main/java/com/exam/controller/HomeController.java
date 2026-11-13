
package com.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Display Home Page
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
