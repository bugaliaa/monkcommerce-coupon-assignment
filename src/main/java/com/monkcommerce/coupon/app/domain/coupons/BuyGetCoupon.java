package com.monkcommerce.coupon.app.domain.coupons;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BuyGetCoupon extends Coupon {
    private Long buyQuantity;
    private Long buyProductId;
    private Long getQuantity;
    private Long getProductId;

    public BuyGetCoupon() {
        this.setType("BUYGET");
    }
}
