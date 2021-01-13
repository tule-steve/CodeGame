package com.codegame.repositories;

import com.codegame.dto.OrderEmailDto;
import com.codegame.model.GiftCard;
import com.codegame.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCodeRepository extends JpaRepository<GiftCard, Long> {

    @Query("select distinct a from GiftCard a where a.giftCode in :codes and a.order.id = :orderId order by a.giftCode")
    List<GiftCard> getRefundCodes(List<String> codes, Long orderId);

    List<GiftCard> findAllByOrder_Id(Long orderId);


    @Query("select distinct a from GiftCard a where a.giftCode in :codes order by a.giftCode")
    List<GiftCard> getCode(List<String> codes);

    @Query("select distinct a.giftCode from GiftCard a where a.item.id = :itemId and a.status = :status")
    List<String> getCodeByItemId1(Long itemId, GiftCard.Status status);

    @Query("select distinct a from GiftCard a where a.item.id = :itemId")
    List<GiftCard> getCodeByItemId(Long itemId);

    @Query("select distinct a from GiftCard a where a.item.id = :itemId and a.status = :status")
    List<GiftCard> getCodeByItemId(Long itemId, GiftCard.Status status);

    @Query("select max(a.autoApprove) from Setting a")
    Boolean isAutoApprove();


    @Query(nativeQuery = true)
    List<OrderEmailDto> getOrderEmailDetail(Long orderId);


    @Query("select a from Setting a")
    List<Setting> getSetting();

}
