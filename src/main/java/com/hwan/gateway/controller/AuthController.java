package com.hwan.gateway.controller;

import com.hwan.gateway.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    @GetMapping("/token")
    public String token(
            @RequestParam String userId,
            @RequestParam String role
    ) {
        return jwtProvider.createToken(userId, role);
    }
}
