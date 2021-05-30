package com.codegame.model;

import com.codegame.dto.ItemDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;

@SqlResultSetMapping(
        name = "itemDetailsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ItemDto.class,
                        columns = {
                                @ColumnResult(name = "itemId"),
                                @ColumnResult(name = "description"),
                                @ColumnResult(name = "count"),
                                @ColumnResult(name = "price", type = Integer.class),
                                @ColumnResult(name = "createdAt", type = Date.class)
                        }
                )
        }
)
@NamedNativeQuery(name = "Item.getItemDetails", query =
        "select itm.id as itemId, itm.description as description, count(gc.id) as count, itm.price as price, itm.date_created as createdAt " +
        " from item itm " +
        " left outer join gift_card gc on itm.id = gc.item_id " +
        " where (?1 is null or ?1 < itm.date_created) and (?2 is null or ?2 > itm.date_created)" +
        "group by itm.id",
        resultSetMapping = "itemDetailsMapping")

@NamedNativeQuery(name = "Item.getThresholdItem", query =
        "select itm.id as itemId, itm.description as description, count(gc.id) as count, null as price, null as createdAt from item itm \n" +
        " left join gift_card gc on itm.id = gc.item_id and gc.status = 'available' \n" +
        " group by itm.id \n" +
        " having count(gc.id)  < ?1",
        resultSetMapping = "itemDetailsMapping")
@Entity
@Data
@Table(name = "item")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Item {

    @Id
    //    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "description id is required.")
    @Column(name = "description")
    String name;

    @Min(1)
    @Column(name = "price")
    int price = 0;

    @Column(name = "is_unpublished")
    Boolean isUnpublished = false;

    @Column(name = "date_created", updatable = false)
    @CreationTimestamp
    protected LocalDateTime dateCreated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    List<GiftCard> codes = new ArrayList<>();

    public void addGiftCard(GiftCard gc) {
        codes.add(gc);
    }
}
