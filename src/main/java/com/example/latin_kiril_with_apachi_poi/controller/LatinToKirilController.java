package com.example.latin_kiril_with_apachi_poi.controller;

import com.example.latin_kiril_with_apachi_poi.payload.ApiResponse;
import com.example.latin_kiril_with_apachi_poi.service.LatinToKirilService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class LatinToKirilController {

    @Autowired
    LatinToKirilService latinToKirilService;

    @PostMapping("/file")
    public HttpEntity<?> file(@RequestParam("file") MultipartFile multipartFile) throws IOException, InvalidFormatException {
        ApiResponse apiResponse = latinToKirilService.translate_and_save(multipartFile);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getfile")
    public ResponseEntity<byte[]> getfile() throws IOException {
        InputStream in = ResourceLoader.class.getClassLoader().getResource("uploads/Result.docx").openStream();
        byte[] bytes = in.readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("Result.docx").build());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/hello")
    public HttpEntity<?> hello() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/test")
    public HttpEntity<?> testt() throws ClassNotFoundException {
        return ResponseEntity.ok("Post method");
    }

}
