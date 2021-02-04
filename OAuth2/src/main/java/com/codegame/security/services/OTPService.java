package com.codegame.security.services;

import com.codegame.security.config.OTPTemplate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    @Autowired
    private OTPTemplate template;

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Integer EXPIRE_MINS = 4;

    private LoadingCache<String, Integer> otpCache;

    public OTPService() {
        super();
        otpCache = CacheBuilder.newBuilder().
                expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
                               .build(new CacheLoader<String, Integer>() {
                                   public Integer load(String key) {
                                       return 0;
                                   }
                               });
    }

    public void sendEmail(String email) throws MessagingException {

        MimeMessage msg = javaMailSender.createMimeMessage();

        int otp = generateOTP(email);

        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("user", "Bro");
        replacements.put("otpnum", String.valueOf(otp));

        String message = template.getTemplate(replacements);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom("tule.java@gmail.com");
        helper.setTo(email);
        helper.setSubject("OTP - Login");
        helper.setText(message, true);

        javaMailSender.send(msg);
    }

    public int generateOTP(String key) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        otpCache.put(key, otp);
        return otp;
    }

    public int getOtp(String key) {
        try {
            return otpCache.get(key);
        } catch (Exception e) {
            return 0;
        }
    }

    public void clearOTP(String key) {
        otpCache.invalidate(key);
    }
}
