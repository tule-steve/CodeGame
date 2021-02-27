package com.codegame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "email")
    String email;

    @Column(name = "total_value")
    Long transactionTotal;

    @JsonIgnore
    @Column(name = "order_detail")
    String orderDetail;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "refund_status")
    @Enumerated(EnumType.STRING)
    GiftCard.Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "refund_date")
    protected LocalDateTime refundDate;

    @Column(name = "created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @Column(name = "is_send_email")
    protected Boolean isSendEmail;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = {CascadeType.PERSIST})
    List<GiftCard> codes = new ArrayList<>();

    public void addGiftCards(List<GiftCard> giftCards, Integer price){
        giftCards.forEach(gc -> {
            gc.setOrder(this);
            gc.setPrice(price);
            gc.setStatus(GiftCard.Status.USED);
        });

        codes.addAll(giftCards);
    }
}
