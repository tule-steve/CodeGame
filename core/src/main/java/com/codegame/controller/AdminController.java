package com.codegame.controller;

import com.codegame.dto.AddGiftCardRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.model.Item;
import com.codegame.model.Setting;
import com.codegame.schedule.ScheduleService;
import com.codegame.services.AdminService;
import com.codegame.specifications.GiftCardFilter;
import com.codegame.specifications.ItemFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    final AdminService adminSvc;

    final ScheduleService scheduleSvc;

    @PostMapping(value = "/approach_refund")
    public ResponseEntity refund(@RequestBody RefundRequest request) {
        adminSvc.refund(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Received the refund request."));
    }

    @GetMapping(value = "/item/{itemId}")
    public Object getCodes(@PathVariable Long itemId, GiftCardFilter filter) {
        return adminSvc.getCodeByItem(itemId, filter);
    }

    @PostMapping(value = "/item/create")
    public ResponseEntity createItem(@RequestBody List<Item> request) {
        adminSvc.createItem(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Created " + request.size() + " items"));
    }

    @PostMapping(value = "/item/update")
    public ResponseEntity updatePrice(@RequestBody List<Item> request) {
        adminSvc.createItem(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated " + request.size() + " items"));
    }

    @PostMapping(value = "/giftcard/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity AddGiftCard(@RequestBody AddGiftCardRequest request) {
        adminSvc.addGiftCard(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("added " + request.getCodes().size() + " gift card"));
    }

    @PostMapping(value = "/giftcard/delete")
    public ResponseEntity deleteGiftCard(@RequestBody List<String> codeList) {
        adminSvc.deleteGiftCard(codeList);
        return ResponseEntity.ok(CommonResponse.buildOkData("deleted " + codeList.size() + " gift card"));
    }

    @GetMapping(value = "/items")
    public List<ItemDto> getItem(@RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                 @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                 @RequestParam(value = "reload", required = false, defaultValue = "false") Boolean isReload) {
        if(isReload){
            scheduleSvc.updateItemData();
        }
        return adminSvc.getItemDetails(from, to);
    }

    @PostMapping(value = "/setting")
    public ResponseEntity updateSetting(@RequestBody Setting request) {
        adminSvc.updateSetting(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated setting"));
    }

    @GetMapping(value = "/setting")
    public Object getSetting() {
        return adminSvc.getSetting();
    }

}
