package com.codegame;

import com.codegame.config.ApiLoggingFilter;
import com.codegame.dto.OrderTemplate;
import com.codegame.dto.ThresholdItemTemplate;
import com.codegame.security.config.OTPTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class CodeGameApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CodeGameApplication.class);
        springApplication.run(args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    //    @Bean
    //    public CorsConfigurationSource corsConfigurationSource() {
    //        CorsConfiguration configuration = new CorsConfiguration();
    //        configuration.setAllowedOrigins(Arrays.asList("*"));
    //        configuration.setAllowedMethods(Arrays.asList("*"));
    //        configuration.setAllowedHeaders(Arrays.asList("*"));
    //
    //        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //        source.registerCorsConfiguration("/**", configuration);
    //        return source;
    //    }

    @Bean
    public OrderTemplate getOrderTempate() throws Exception {
        return new OrderTemplate();
    }

    @Bean
    public ThresholdItemTemplate getThresholdTemplate() throws Exception {
        return new ThresholdItemTemplate();
    }

    @Bean
    public FilterRegistrationBean<ApiLoggingFilter> loggingFilter() {
        FilterRegistrationBean<ApiLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiLoggingFilter("requestId"));
        return registrationBean;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.basicAuthentication("ck_d7aff76724444212194ad9326097da4cdc874d8c",
                                           "cs_4a6d60dc9256359ae93ee5c8eaf2c55f94264075").build();
    }

}
