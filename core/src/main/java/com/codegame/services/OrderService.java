package com.codegame.services;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.LineItemDto;
import com.codegame.dto.RefundLineItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import com.codegame.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    final ItemRepository itemRepo;

    final GiftCodeRepository giftCodeRepo;

    final OrderRepository orderRepo;


    public List<Order> getOrderList(){
        return orderRepo.findAll();
    }

    public List<GiftCard> getOrderDetail(Long orderId){
        return giftCodeRepo.findAllByOrder_Id(orderId);
    }

    public void createOrder(CreateOrderRequest request) {
        Order newOrder = new Order();
        newOrder.setOrderId(request.getOrderId());
        newOrder.setEmail(request.getEmail());
        newOrder.setOrderDetail(request.toString());
        newOrder.setTransactionTotal(request.getTransactionTotal());

        for (LineItemDto lineItm : request.getLineItems()) {
            Item currItm = itemRepo.findById(lineItm.getItemId())
                                   .orElseThrow(() -> new GlobalValidationException(
                                           "Cannot find item with id " + lineItm.getItemId()));

            List<GiftCard> availGiftCard = giftCodeRepo.getCodeByItemId(currItm.getId(),
                                                                        GiftCard.Status.AVAILABLE);
            if (availGiftCard.size() < lineItm.getAmount()) {
                throw new GlobalValidationException("Not enough gift code for item with id " + lineItm.getItemId());
            }
            newOrder.addGiftCards(availGiftCard.subList(0, lineItm.getAmount()), lineItm.getPrice());
        }

        orderRepo.save(newOrder);
    }

    public void refundOrder(RefundRequest request) {
        Order refundOrder = orderRepo.findByOrderId(request.getOrderId());
        if(refundOrder == null){
            throw new GlobalValidationException("Cannot find the order");
        }

        List<GiftCard> refundCodes = giftCodeRepo.getRefundCodes(request.getCodes()
                                                                        .stream()
                                                                        .map(RefundLineItemDto::getCode)
                                                                        .collect(
                                                                                Collectors.toList()),
                                                                 refundOrder.getId(),
                                                                 GiftCard.Status.USED);

        int refundCodeCount = request.getCodes().size();
        int existingCodeCount = refundCodes.size();

        if (refundCodeCount != existingCodeCount) {
            throw new GlobalValidationException("Code list not matching");
        }

        Boolean isAutoApprove = giftCodeRepo.isAutoApprove();
        GiftCard.Status newStatus = GiftCard.Status.REFUNDING;
        if(isAutoApprove){
            newStatus = GiftCard.Status.APPROVED_FOR_REFUND;
        }

        GiftCard.Status finalNewStatus = newStatus;
        refundCodes.forEach(r -> {
            r.setStatus(finalNewStatus);
            request.getCodes().stream().forEach(data -> {
                if(data.getCode().equals(r.getGiftCode())){
                    r.setRefundType(data.getRefundType());
                }
            });

        });

        refundOrder.setRefundDate(LocalDateTime.now());
        refundOrder.setStatus(GiftCard.Status.REFUNDING);
        orderRepo.save(refundOrder);
        giftCodeRepo.saveAll(refundCodes);
    }
}
