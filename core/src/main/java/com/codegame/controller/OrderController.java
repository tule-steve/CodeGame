package com.codegame.controller;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.RefundRequest;
import com.codegame.services.OrderService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    final OrderService orderSvc;

    @GetMapping(value = "/list")
    public Object list() {
        return orderSvc.getOrderList();
    }

    @PostMapping(value = "/create")
    public Object createOrder(@Validated @RequestBody CreateOrderRequest request) {
        orderSvc.createOrder(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("received the order request"));
    }


    @PostMapping(value = "/refund")
    public Object refundOrder(@RequestBody RefundRequest request) {
        orderSvc.refundOrder(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("received the request to refund order"));
    }
}
