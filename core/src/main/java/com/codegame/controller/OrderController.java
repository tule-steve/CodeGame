package com.codegame.controller;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.RefundRequest;
import com.codegame.model.GiftCard;
import com.codegame.services.OrderService;
import com.codegame.specifications.GiftCardFilter;
import com.codegame.specifications.OrderFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    final OrderService orderSvc;

    @GetMapping(value = "/list")
    public Object list(OrderFilter filter) {
        return orderSvc.getOrderList(filter);
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

    @GetMapping(value = "/detail/{orderId}")
    public Object getOrderDetail(@PathVariable Long orderId, GiftCardFilter filter) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<GiftCard> result = orderSvc.getOrderDetail(orderId, filter);
        if (user == null ||
            user.getAuthorities().stream().noneMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getAuthority()))) {
            result.forEach(r -> r.setGiftCode(r.getMaskCode()));
        }
        return result;
    }
}
