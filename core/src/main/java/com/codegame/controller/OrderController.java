package com.codegame.controller;

import com.codegame.dto.ChangeStatusItemRequest;
import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.GetHistoryRequest;
import com.codegame.dto.RefundRequest;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.services.AdminService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    final OrderService orderSvc;

    final GiftCodeRepository giftRepo;

    private final AdminService adminSvc;

    @GetMapping(value = "/list")
    public Object list(OrderFilter filter) {
        return orderSvc.getOrderList(filter);
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

    @PostMapping(value = "/create")
    public Object createOrder(@Validated @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderSvc.createOrder(request));
    }


    @GetMapping(value = "/check-item/{itemId}")
    public Object getGiftCount(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();
        response.put("item", itemId);
        response.put("amount", giftRepo.countAvailable(itemId));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/item/update-status")
    public Object getGiftCount(@RequestBody @Validated ChangeStatusItemRequest request) {
        adminSvc.updateItemStatus(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated status"));
    }

    @PostMapping(value = "/history")
    public Object getHistory(@Validated @RequestBody GetHistoryRequest request) {
        adminSvc.getHistory(request);
        return ResponseEntity.ok(adminSvc.getHistory(request));
    }

}
