package com.example.controller;

import com.example.common.Result;
import com.example.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/send-code")
    public Result sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return registerService.sendVerificationCode(email);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("verificationCode");
        return registerService.registerUser(email, code);
    }
}