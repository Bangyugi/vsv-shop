package com.bangvan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Tag(name = "Hello", description = "Hello")
public class HelloController {
    @Operation(summary = "Hello world",description = "Hello world")
    @GetMapping()
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello world");
    }
}
