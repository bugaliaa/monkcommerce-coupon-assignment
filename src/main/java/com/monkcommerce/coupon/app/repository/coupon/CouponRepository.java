package com.monkcommerce.coupon.app.repository.coupon;

import java.util.List;
import java.util.Optional;

import com.monkcommerce.coupon.app.domain.coupons.BuyGetCoupon;
import com.monkcommerce.coupon.app.domain.coupons.CartCoupon;
import com.monkcommerce.coupon.app.domain.coupons.Coupon;
import com.monkcommerce.coupon.app.domain.coupons.ProductCoupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface CouponRepository extends MongoRepository<Coupon, String> {
    @Query("{ 'type': 'BUYGET', 'active': true, 'buyQuantity': ?0, 'buyProductId': ?1, 'getQuantity': ?2, 'getProductId': ?3 }")
    Optional<BuyGetCoupon> findActiveBuyGetCoupon(Long buyQuantity, Long buyProductId, Long getQuantity, Long getProductId);

    @Query("{ 'type': 'PRODUCT', 'active': true, 'productId': ?0, 'discount': ?1 }")
    Optional<ProductCoupon> findActiveProductCoupon(Long productId, Double discount);

    @Query("{ 'type': 'CART', 'active': true, 'cartValue': ?0, 'discount': ?1 }")
    Optional<CartCoupon> findActiveCartCoupon(Double cartValue, Double discount);

    boolean existsByCouponCode(String couponCode);

    List<Coupon> findByActiveTrue();

    Optional<Coupon> findByCouponCode(String couponCode);
}
