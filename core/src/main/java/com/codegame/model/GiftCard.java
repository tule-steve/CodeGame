package com.codegame.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "tu_test_gc")
public class GiftCard {

    public enum GiftCardStatus {
        USED,
        NOT_USED,
        REFUNDING,
        REFUNDED;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;


    @Column(name = "gift_code")
    String giftCode;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    GiftCardStatus status;

    //ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", updatable = false, nullable = false)
    Item item;
}
