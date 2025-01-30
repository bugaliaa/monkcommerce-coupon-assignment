package com.monkcommerce.coupon.app.web.dto.request.coupon;

import lombok.Data;

@Data
public class CreateCouponRequestDTO {
    private String type;
    private String name;
    private String couponCode;
    private boolean active;

    private Long productId;
    private Double discount;
    private Double cartValue;
    private Long buyQuantity;
    private Long buyProductId;
    private Long getQuantity;
    private Long getProductId;
}
