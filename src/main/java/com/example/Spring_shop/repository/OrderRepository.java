package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrder(@Param("email") String email, Pageable pageable);

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email") String email);

    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " left join fetch o.member m" +
            " where o.id = :id")
    Optional<Order> findOrderAndPaymentAndMember(Long id);

    @Query("select o from Order o" +
            " left join fetch o.payment p" +
            " where o.orderUid = :orderUid")
    Optional<Order> findOrderAndPayment(String orderUid);
}
