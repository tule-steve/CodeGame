package com.codegame.specifications;

import com.codegame.model.GiftCard;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GiftCardFilter implements Specification<GiftCard> {
    private Long itemId;

    private Long orderId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    @Override
    public Predicate toPredicate(Root<GiftCard> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (itemId != null) {
            predicates.add(builder.equal(root.get("item").get("id"), itemId));
        }
        if (orderId != null) {
            predicates.add(builder.equal(root.get("order").get("id"), orderId));
        }
        if (from != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay()));
        }
        if (to != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), to.atTime(LocalTime.MAX)));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

