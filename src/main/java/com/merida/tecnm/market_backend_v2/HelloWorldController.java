package com.merida.tecnm.market_backend_v2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/saludo")
    public String helloWorld(){
        return "Hello World!";
    }
}
