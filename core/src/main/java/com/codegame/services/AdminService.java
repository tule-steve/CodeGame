package com.codegame.services;

import com.codegame.dto.*;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.model.Setting;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import com.codegame.repositories.OrderRepository;
import com.codegame.specifications.GiftCardFilter;
import com.codegame.specifications.ItemFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final GiftCodeRepository giftRepo;

    final ItemRepository itemRepo;

    final OrderRepository orderRepo;

    final EntityManager em;

    final GiftCodeRepository gcRepo;

    public void refund(RefundRequest request) {

        Order refundOrder = orderRepo.findByOrderId(request.getOrderId());
        if (refundOrder == null) {
            throw new GlobalValidationException("Cannot find the order");
        }

        List<GiftCard> refundCodes = giftRepo.getRefundCodes(request.getCodes()
                                                                    .stream()
                                                                    .map(r -> r.getCode())
                                                                    .collect(Collectors.toList()),
                                                             refundOrder.getId());
        int refundCodeCount = request.getCodes().size();
        int existingCodeCount = refundCodes.size();

        if (refundCodeCount != existingCodeCount) {
            throw new GlobalValidationException("Code list not matching");
        }

        //        List<GiftCard> refundByMoneyLst = refundCodes.stream()
        //                                                      .filter(r -> GiftCard.RefundType.BY_MONEY.equals(r.getRefundType()))
        //                                                      .collect(Collectors.toList());
        //
        //        List<GiftCard> refundByKeyLst = refundCodes.stream()
        //                                                      .filter(r -> GiftCard.RefundType.BY_KEY.equals(r.getRefundType()))
        //                                                      .collect(Collectors.toList());

        refundCodes.forEach(r -> {
            r.setStatus(GiftCard.Status.APPROVED_FOR_REFUND);
            request.getCodes().stream().forEach(data -> {
                if (data.getCode().equals(r.getGiftCode())) {
                    r.setRefundType(data.getRefundType());
                    if (r.getRefundType().equals(GiftCard.RefundType.BY_MONEY)) {
                        r.setStatus(GiftCard.Status.REFUNDED);
                    }
                }
            });

        });
        refundOrder.setStatus(GiftCard.Status.APPROVED_FOR_REFUND);
        orderRepo.save(refundOrder);
        giftRepo.saveAll(refundCodes);
    }

    public List<GiftCard> getCodeByItem(Long itemId, GiftCardFilter filter) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new GlobalValidationException("Item not exist"));
        filter.setItemId(item.getId());
        return giftRepo.findAll(filter);
    }

    public void createItem(List<Item> data) {
        itemRepo.saveAll(data);
    }

    public void addGiftCard(AddGiftCardRequest data) {
        Item item = itemRepo.findById(data.getItemId())
                            .orElseThrow(() -> new GlobalValidationException("Item not exist"));

        List<GiftCard> newGiftCardList = new ArrayList<>();
        for (String code : data.getCodes()) {
            GiftCard gc = new GiftCard();
            gc.setGiftCode(code);
            gc.setItem(item);
            gc.setStatus(GiftCard.Status.AVAILABLE);
            newGiftCardList.add(gc);
            item.addGiftCard(gc);
        }

        giftRepo.saveAll(newGiftCardList);
    }

    public List<ItemDto> getItemDetails(LocalDate from, LocalDate to) {
        LocalDateTime fromLDT = from != null ? from.atStartOfDay() : null;
        LocalDateTime toLDT = to != null ? to.atTime(LocalTime.MAX) : null;
        return itemRepo.getItemDetails(fromLDT, toLDT);
    }

    public void deleteGiftCard(List<String> codeList) {
        List<GiftCard> deletedCodes = giftRepo.getCode(codeList);
        int count = codeList.size();
        int existingCodeCount = deletedCodes.size();

        if (count != existingCodeCount) {
            throw new GlobalValidationException("Code list not matching");
        }

        //        List<GiftCard> refundByMoneyLst = refundCodes.stream()
        //                                                      .filter(r -> GiftCard.RefundType.BY_MONEY.equals(r.getRefundType()))
        //                                                      .collect(Collectors.toList());
        //
        //        List<GiftCard> refundByKeyLst = refundCodes.stream()
        //                                                      .filter(r -> GiftCard.RefundType.BY_KEY.equals(r.getRefundType()))
        //                                                      .collect(Collectors.toList());

        deletedCodes.forEach(r -> {
            r.setOldStatus(r.getStatus());
            r.setStatus(GiftCard.Status.DELETED);
        });
        giftRepo.saveAll(deletedCodes);
    }

    public void updateSetting(Setting newData) {
        newData.setId(1L);
        em.merge(newData);
    }

    public Setting getSetting() {
        List<Setting> settings = giftRepo.getSetting();
        if (settings.isEmpty()) {
            throw new GlobalValidationException("no setting is set up");
        }
        return settings.get(0);
    }

    public void updateItemStatus(ChangeStatusItemRequest request) {
        Item item = itemRepo.findById(request.getItemId())
                            .orElseThrow(() -> new GlobalValidationException("cannot file the item"));
        item.setIsUnpublished(request.getIsUnpublished());
        itemRepo.save(item);
    }

    public Map<Long, List<OrderEmailDto>> getHistory(GetHistoryRequest request) {
        List<Order> orders;
        if(request.getOrderIds().size() > 0){
            orders  = orderRepo.findDistinctByEmailAndOrderIdIn(request.getEmail(), request.getOrderIds());
        } else {
            orders  = orderRepo.findDistinctByEmail(request.getEmail());
        }

        Map<Long, List<OrderEmailDto>> response = new HashMap<>();
        orders.forEach(r -> {
            response.put(r.getOrderId(), gcRepo.getOrderEmailDetail(r.getId()));
        });
        return response;
    }
}
