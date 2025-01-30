package com.monkcommerce.coupon.app.web.dto.request.coupon;

import lombok.Data;

@Data
public class UpdateCouponRequestDTO {
    private String couponCode;
    private Boolean active;
    private Long buyQuantity;
    private Long buyProductId;
    private Long getQuantity;
    private Long getProductId;
    private Long productId;
    private Double discount;
    private Double cartValue;
}
