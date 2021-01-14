package com.codegame.repositories;

import com.codegame.dto.ItemDto;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query(nativeQuery = true)
    List<ItemDto> getItemDetails(LocalDateTime from, LocalDateTime to);
}
