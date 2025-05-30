package com.rip.notification_service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greet")
public class HelloController {

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String greet() {
        return "Hello World!";
    }
}
