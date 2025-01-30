package com.monkcommerce.coupon.app.domain.coupons;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CartCoupon extends Coupon {
    private Double cartValue;
    private Double discount;

    public CartCoupon() {
        this.setType("CART");
    }
}
