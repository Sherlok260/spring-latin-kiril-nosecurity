package com.example.latin_kiril_with_apachi_poi.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class KirilToLatinController {

    @GetMapping("/helloo")
    public HttpEntity<?> helloltok() {
        return ResponseEntity.ok("Hello From LatinToKiril controller");
    }

}
