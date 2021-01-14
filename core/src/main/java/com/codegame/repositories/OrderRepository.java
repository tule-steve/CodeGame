package com.codegame.repositories;

import com.codegame.dto.OrderEmailDto;
import com.codegame.model.GiftCard;
import com.codegame.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Order findByOrderId(Long orderId);

    List<Order> findAllByIsSendEmailFalse();

}
