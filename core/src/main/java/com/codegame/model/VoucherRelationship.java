package com.codegame.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * A class which represents the voucher_relationship table in the qrsvc database.
 */
@javax.persistence.Entity
@Table(name = "voucher_relationship")
@Data
public class VoucherRelationship {

    @Id
//    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "lft")
    private Long left;

    @Column(name = "rgt")
    private Long right;

    @Column(name = "ref_lft")
    private Long refLeft;

    @Column(name = "ref_rgt")
    private Long refRight;

    @Column(name = "is_further_link")
    private boolean isFurtherLink;

    @Formula(value="sets")
    private String sets;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;


    public void moveToRight(long distance){
        left +=  distance;
        right += distance;

    }
}
