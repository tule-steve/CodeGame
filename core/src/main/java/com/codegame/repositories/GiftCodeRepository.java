package com.codegame.repositories;

import com.codegame.model.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCard, Long> {

    @Query("select distinct a from GiftCard a where a.giftCode in :codes and a.orderId = :orderId")
    List<GiftCard> getRefundCodes(List<String> codes, Long orderId);

    @Query("select distinct a.giftCode from GiftCard a where a.item.id = :itemId and a.status = :status")
    List<String> getCodeByItemId(Long itemId, GiftCard.GiftCardStatus status);
}
