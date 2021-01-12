package com.codegame.model;

import com.codegame.dto.ItemDto;
import com.codegame.dto.OrderEmailDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static javax.persistence.GenerationType.IDENTITY;


@SqlResultSetMapping(
        name = "OrderEmailDataMapping",
        classes = {
                @ConstructorResult(
                        targetClass = OrderEmailDto.class,
                        columns = {
                                @ColumnResult(name = "itemId"),
                                @ColumnResult(name = "description"),
                                @ColumnResult(name = "price"),
                                @ColumnResult(name = "count"),
                                @ColumnResult(name = "codes")
                        }
                )
        }
)
@NamedNativeQuery(name = "GiftCard.getOrderEmailDetail", query =
        "select itm.id as itemId, itm.description as description, GROUP_CONCAT(gc.gift_code) as codes, count(gc.id) as count, itm.price as price " +
        " from tu_test_itm itm " +
        " inner join tu_test_gc gc on itm.id = gc.item_id " +
        " where gc.order_id = :orderId "+
        " group by itm.id",
        resultSetMapping = "OrderEmailDataMapping")
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    public String getMaskCode(){
        if(giftCode.length() < 7){
            return giftCode;
        }

        char[] tokenChars = giftCode.toCharArray();
        giftCode.getChars(tokenChars.length -6, tokenChars.length, tokenChars, 0);
        Arrays.fill(tokenChars, 0, tokenChars.length -6, '*');

        return(new String(tokenChars));
    }

}
