package com.codegame.schedule;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.LineItemDto;
import com.codegame.dto.OrderEmailDto;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.model.Setting;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import com.codegame.repositories.OrderRepository;
import com.codegame.services.AdminService;
import com.codegame.services.EmailService;
import com.codegame.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderRequest.class);

    final private OrderRepository orderRepo;

    final private GiftCodeRepository gcRepo;

    final private EmailService emailSvc;

    private final RestTemplate restTemplate;

    final AdminService adminSvc;

    final ItemRepository itemRepo;

    final OrderService orderSvc;

    final GiftCodeRepository giftCodeRepo;

    @Scheduled(fixedRate = 15000)
    public void sendOrderEmail() {
        List<Order> unsentOrder = orderRepo.findAllByIsSendEmailFalse();
        for (Order order : unsentOrder) {
            try {
                List<OrderEmailDto> detail = gcRepo.getOrderEmailDetail(order.getId());
                emailSvc.sendEmail(order, detail);
                order.setIsSendEmail(true);
                orderRepo.save(order);
            } catch (Exception ex) {
                logger.error("error", ex);
            }
        }

    }

    @Scheduled(fixedRate = 360000)
    public void updateItemData() {
        //        HttpHeaders headers = new HttpHeaders();
        //        String auth = "ck_d7aff76724444212194ad9326097da4cdc874d8c" + ":" + "cs_4a6d60dc9256359ae93ee5c8eaf2c55f94264075";
        //        byte[] encodedAuth = Base64.encodeBase64(
        //                auth.getBytes(Charset.forName("US-ASCII")) );
        //        String authHeader = "Basic " + new String( encodedAuth );
        //        headers.set( "Authorization", authHeader );
        try {
            Item[] itemData;
            int i = 0;
            do {
                i++;
                itemData = restTemplate.getForEntity(
                        "https://keysgame.vn/wp-json/wc/v3/products?per_page=100& page=" + i,
                        Item[].class).getBody();
                adminSvc.createItem(Arrays.asList(itemData));
            } while (itemData.length > 0);

        } catch (HttpStatusCodeException e) {

            logger.error("error on getting items", ResponseEntity.status(e.getRawStatusCode())
                                                                 .headers(e.getResponseHeaders())
                                                                 .body(e.getResponseBodyAsString()));
        }
    }

    @Scheduled(fixedRate = 15000)
    public void processApprovedRefund() {
        List<Order> unsentOrder = orderRepo.findAllByStatus(GiftCard.Status.APPROVED_FOR_REFUND);
        for (Order order : unsentOrder) {
            try {
                orderSvc.refundOrder(order);
            } catch (Exception ex) {
                logger.error("error", ex);
            }
        }
    }

    @Scheduled(cron = "0 7 1 * * ?")
    public void notifyThresholdItem() throws MessagingException {
        Setting setting = adminSvc.getSetting();
        List<ItemDto> items = itemRepo.getThresholdItem(setting.getMinAmountAlert());
        emailSvc.notifyThresholdItem(items);
    }

}
