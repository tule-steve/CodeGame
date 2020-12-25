package com.codegame.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/payment/callback")
@RequiredArgsConstructor
public class PaymentController {

    @GetMapping(value = "/user-info")
    public Object getLoginUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
