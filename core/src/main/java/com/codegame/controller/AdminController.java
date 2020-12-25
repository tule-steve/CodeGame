package com.codegame.controller;

import com.codegame.dto.AddGiftCardRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.model.Item;
import com.codegame.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    final AdminService adminSvc;

    @PostMapping(value = "/refund")
    public ResponseEntity refund(@RequestBody RefundRequest request) {
        adminSvc.refund(request);
        return ResponseEntity.ok("Received the refund request.");
    }

    @GetMapping(value = "/item/{itemId}")
    public Object getCodes(@PathVariable Long itemId) {
        return adminSvc.getCodeByItem(itemId);
    }

    @PostMapping(value = "/item/create")
    public ResponseEntity createItem(@RequestBody Item request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        adminSvc.createItem(request);
        return ResponseEntity.ok("created item");
    }

    @PostMapping(value = "/giftcard/create")
    public ResponseEntity addGiftCard(@RequestBody AddGiftCardRequest request) {
        adminSvc.addGiftCard(request);
        return ResponseEntity.ok("added " + request.getCodes().size() + " gift card");
    }

    @GetMapping(value = "/items")
    public List<ItemDto> getCodes() {
        return adminSvc.getItemDetails();
    }
}
