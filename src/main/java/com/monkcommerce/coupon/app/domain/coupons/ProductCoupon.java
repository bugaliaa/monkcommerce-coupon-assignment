package com.monkcommerce.coupon.app.domain.coupons;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCoupon extends Coupon {
    private Long productId;
    private Double discount;

    public ProductCoupon() {
        this.setType("PRODUCT");
    }
}
