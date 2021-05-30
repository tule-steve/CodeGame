package com.codegame.services;

import com.codegame.dto.*;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import com.codegame.repositories.OrderRepository;
import com.codegame.specifications.GiftCardFilter;
import com.codegame.specifications.OrderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    final ItemRepository itemRepo;

    final GiftCodeRepository giftCodeRepo;

    final OrderRepository orderRepo;

    final private EmailService emailSvc;

    public List<Order> getOrderList(OrderFilter filter){
        return orderRepo.findAll(filter);
    }

    public List<GiftCard> getOrderDetail(Long orderId, GiftCardFilter filter){
        Order order = orderRepo.findByOrderId(orderId);
        if(order == null){
            throw new GlobalValidationException("Cannot find the order");
        }
        filter.setOrderId(order.getId());
        return giftCodeRepo.findAll(filter);
    }

    public Map<Long, List<String>> createOrder(CreateOrderRequest request) {
        if(orderRepo.findByOrderId(request.getOrderId()) != null){
            throw new GlobalValidationException("order " + request.getOrderId() + " is created before");
        }
        Order newOrder = new Order();
        newOrder.setOrderId(request.getOrderId());
        newOrder.setEmail(request.getEmail());
        newOrder.setOrderDetail(request.toString());
        newOrder.setTransactionTotal(request.getTransactionTotal());
        newOrder.setStatus(GiftCard.Status.CREATED);
        newOrder = orderRepo.save(newOrder);

        List<GiftCard> availGiftCard;
        Item currItm;
        Map<Long, List<String>> response = new HashMap<>();
        List<String> codeList;
        List<GiftCard> giftCodeList;
        for (LineItemDto lineItm : request.getLineItems()) {
            currItm = itemRepo.findById(lineItm.getItemId())
                                   .orElseThrow(() -> new GlobalValidationException(
                                           "Cannot find item with id " + lineItm.getItemId()));


            availGiftCard = giftCodeRepo.getCodeByItemId(currItm.getId(),
                                                                        GiftCard.Status.AVAILABLE);
            if (availGiftCard.size() < lineItm.getAmount()) {
                throw new GlobalValidationException("Not enough gift code for item with id " + lineItm.getItemId());
            }
            giftCodeList = availGiftCard.subList(0, lineItm.getAmount());
            codeList = giftCodeList.stream().map(GiftCard::getGiftCode).collect(Collectors.toList());
            response.put(currItm.getId(), codeList);
            newOrder.addGiftCards(giftCodeList, lineItm.getPrice());
        }

        return response;
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
                                                                 refundOrder.getId());

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

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void refundOrder(Order order) throws MessagingException {
        List<String> refundedCodeList = new ArrayList<>();
        List<OrderEmailDto> details = giftCodeRepo.getRefundEmailDetail(order.getId());

        for (OrderEmailDto detail : details) {
            Item currItm = itemRepo.findById(detail.getItemId())
                                   .orElseThrow(() -> new GlobalValidationException(
                                           "Cannot find item with id " + detail.getItemId()));

            List<GiftCard> availGiftCard = giftCodeRepo.getCodeByItemId(currItm.getId(),
                                                                        GiftCard.Status.AVAILABLE);
            if (availGiftCard.size() < detail.getCount().intValue()) {
                throw new GlobalValidationException("Not enough gift code for item with id " + detail.getItemId());
            }
            refundedCodeList.addAll(Arrays.asList(detail.getCodes().split(",")));
            List<GiftCard> newGiftCodeList = availGiftCard.subList(0, detail.getCount().intValue());
            newGiftCodeList.forEach(r -> {
                r.setStatus(GiftCard.Status.USED);
                r.setOrder(order);
            });
            giftCodeRepo.saveAll(newGiftCodeList);
            StringBuilder sb = new StringBuilder();
            newGiftCodeList.forEach(r -> sb.append(r.getGiftCode() + ", "));
            detail.setCodes(sb.toString());
        }
        giftCodeRepo.updateRefundedCodes(GiftCard.Status.REFUNDED, refundedCodeList);
        order.setStatus(GiftCard.Status.REFUNDED);
        orderRepo.save(order);
        emailSvc.sendEmail(order, details);

    }
}
