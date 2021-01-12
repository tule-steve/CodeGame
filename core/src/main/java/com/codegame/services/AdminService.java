package com.codegame.services;

import com.codegame.dto.AddGiftCardRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Setting;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final GiftCodeRepository giftRepo;

    final ItemRepository itemRepo;

    final EntityManager em;

    public void refund(RefundRequest request) {
        List<GiftCard> refundCodes = giftRepo.getRefundCodes(request.getCodes()
                                                                    .stream()
                                                                    .map(r -> r.getCode())
                                                                    .collect(Collectors.toList()),
                                                             request.getOrderId(),
                                                             GiftCard.Status.REFUNDING);
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
                if(data.getCode().equals(r.getGiftCode())){
                    r.setRefundType(data.getRefundType());
                }
            });

        });
        giftRepo.saveAll(refundCodes);
    }

    public List<GiftCard> getCodeByItem(Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new GlobalValidationException("Item not exist"));
        return giftRepo.getCodeByItemId(item.getId());
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

    public List<ItemDto> getItemDetails() {

        return itemRepo.getItemDetails();
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
    

    public void updateSetting(Setting newData){
        newData.setId(0L);
        em.merge(newData);
    }

    public Object getSetting(){
        List<Setting> settings = giftRepo.getSetting();
        if(settings.isEmpty()){
            throw new GlobalValidationException("no setting is set up");
        }
        return settings.get(0);
    }
}
