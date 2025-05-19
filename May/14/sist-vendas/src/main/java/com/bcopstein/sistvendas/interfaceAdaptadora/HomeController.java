package com.bcopstein.sistvendas.interfaceAdaptadora;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Use @Controller, não @RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/welcome.html";
    }
}