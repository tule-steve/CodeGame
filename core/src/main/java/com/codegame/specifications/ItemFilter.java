package com.codegame.specifications;

import com.codegame.model.Item;
import com.codegame.model.Order;
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
public class ItemFilter implements Specification<Item> {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate refundFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate refundTo;

    @Override
    public Predicate toPredicate(Root<Item> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (from != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay()));
        }
        if (to != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), to.atTime(LocalTime.MAX)));
        }
        if (refundFrom != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("refundDate"), refundFrom.atStartOfDay()));
        }
        if (refundTo != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("refundDate"), refundTo.atTime(LocalTime.MAX)));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

