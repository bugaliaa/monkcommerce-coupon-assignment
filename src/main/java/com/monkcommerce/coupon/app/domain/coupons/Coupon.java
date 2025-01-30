package com.monkcommerce.coupon.app.domain.coupons;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "coupons")
public abstract class Coupon {
    @Id
    private String couponId;

    private String type;
    private String couponCode;
    private String name;
    private boolean active;

    private long createdAt;
    private long updatedAt;
}
