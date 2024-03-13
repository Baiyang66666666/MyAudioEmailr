package com.com6103.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class BaseController {
    @GetMapping("/")
    public String loading(Model model) {
        return "loading";
    }
}
