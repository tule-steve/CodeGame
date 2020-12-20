package com.codegame.controller;

import com.codegame.model.VoucherRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/payment/callback")
@RequiredArgsConstructor
public class PaymentController {

    @GetMapping(value = "/user-info")
    public Object getLoginUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
