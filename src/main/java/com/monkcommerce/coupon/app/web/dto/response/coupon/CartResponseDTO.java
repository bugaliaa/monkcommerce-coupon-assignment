package com.monkcommerce.coupon.app.web.dto.response.coupon;

import java.util.List;

import com.monkcommerce.coupon.app.web.dto.request.coupon.CartRequestDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDTO {
    private List<CartRequestDTO.CartItemDTO> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
    private CouponDTO appliedCoupon;

    @Data
    @Builder
    public static class CouponDTO {
        private String couponCode;
        private String type;
        private String name;
        private double discount;
    }
}
