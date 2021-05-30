package com.codegame.services;

import com.codegame.dto.ItemDto;
import com.codegame.dto.OrderEmailDto;
import com.codegame.dto.OrderTemplate;
import com.codegame.dto.ThresholdItemTemplate;
import com.codegame.model.Order;
import com.codegame.repositories.OrderRepository;
import com.codegame.security.config.OTPTemplate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    final private OrderTemplate template;

    final private ThresholdItemTemplate thresholdItemTemplate;

    final private JavaMailSender javaMailSender;

    public void sendEmail(Order order, List<OrderEmailDto> detail) throws MessagingException {


        MimeMessage msg = javaMailSender.createMimeMessage();


        String message = template.buildNewLineForOrder(order, detail);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom("stephenle1412@gmail.com");
        helper.setTo(order.getEmail());
        helper.setSubject("Order: " + order.getOrderId());
        helper.setText(message, true);

        javaMailSender.send(msg);
    }

    public void notifyThresholdItem(List<ItemDto> detail) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();


        String message = thresholdItemTemplate.buildTemplate(detail);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom("stephenle1412@gmail.com");
        helper.setTo("stephenle1412@gmail.com");
        helper.setSubject("Cảnh báo về item sắp hết hàng");
        helper.setText(message, true);

        javaMailSender.send(msg);
    }
}
