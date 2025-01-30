package com.monkcommerce.coupon.app.web.dto.request.coupon;

import java.util.List;

import lombok.Data;

@Data
public class CartRequestDTO {
    private CartDTO cart;

    @Data
    public static class CartDTO {
        private List<CartItemDTO> items;
    }

    @Data
    public static class CartItemDTO {
        private Long productId;
        private int quantity;
        private double price;
    }
}
