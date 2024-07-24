package com.example.Spring_shop.repository;

import com.example.Spring_shop.dto.CartDetailDto;
import com.example.Spring_shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.example.Spring_shop.dto.CartDetailDto(ci.id, i.itemNm, ci.price, ci.count, " +
            "im.imgUrl, i.BidPrice,ci.lowestBidPrice, i.endDate, i.id) " +
            "from CartItem ci,ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id "+
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);

}
