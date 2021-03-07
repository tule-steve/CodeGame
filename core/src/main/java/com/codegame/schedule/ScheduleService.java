package com.codegame.schedule;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.OrderEmailDto;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.OrderRepository;
import com.codegame.services.AdminService;
import com.codegame.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CreateOrderRequest.class);

    final private OrderRepository orderRepo;

    final private GiftCodeRepository gcRepo;

    final private EmailService emailSvc;

    private final RestTemplate restTemplate;

    final AdminService adminSvc;

    @Scheduled(fixedRate = 1000)
    public void sendOrderEmail() {
        List<Order> unsentOrder = orderRepo.findAllByIsSendEmailFalse();
        for (Order order : unsentOrder) {
            try {
                List<OrderEmailDto> detail = gcRepo.getOrderEmailDetail(order.getId());
                emailSvc.sendEmail(order, detail);
                order.setIsSendEmail(false);
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
            Item[] itemData = restTemplate.getForEntity("https://keysgame.vn/wp-json/wc/v3/products",
                                                        Item[].class).getBody();
            adminSvc.createItem(Arrays.asList(itemData));
        } catch (HttpStatusCodeException e) {
            ResponseEntity.status(e.getRawStatusCode())
                          .headers(e.getResponseHeaders())
                          .body(e.getResponseBodyAsString());
        }
    }
}
