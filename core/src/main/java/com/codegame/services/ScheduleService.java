package com.codegame.services;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.OrderEmailDto;
import com.codegame.model.Order;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderRequest.class);

    final private OrderRepository orderRepo;

    final private GiftCodeRepository gcRepo;

    final private EmailService emailSvc;

//    @Scheduled(fixedRate = 1000)
    public void sendOrderEmail() {
        List<Order> unsentOrder = orderRepo.findAllByIsSendEmailFalse();

        try {
            for (Order order : unsentOrder) {
                order = unsentOrder.get(0);
                List<OrderEmailDto> detail = gcRepo.getOrderEmailDetail(order.getId());
                emailSvc.sendEmail(order, detail);
            }
        } catch (Exception ex) {
            logger.error("error", ex);
        }
    }
}
