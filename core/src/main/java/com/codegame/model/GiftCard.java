package com.codegame.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "tu_test_gc")
public class GiftCard {

    public enum Status {
        AVAILABLE,
        USED,
        NOT_USED,
        REFUNDING,
        APPROVED_FOR_REFUND,
        REFUNDED,
        DELETED;
    }

    public enum RefundType {
        BY_MONEY,
        BY_KEY;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;


    @Column(name = "gift_code")
    String giftCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;

    @JsonIgnore
    @Column(name = "old_status")
    @Enumerated(EnumType.STRING)
    Status oldStatus;

    @Column(name = "refund_type")
    @Enumerated(EnumType.STRING)
    RefundType refundType;

    @Column(name = "price")
    Integer price;

    @Column(name = "created_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    LocalDateTime createdAt;

    //ManyToOne
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", updatable = false, nullable = false)
    Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    Order order;

}
