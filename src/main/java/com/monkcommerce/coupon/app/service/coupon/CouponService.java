package com.monkcommerce.coupon.app.service.coupon;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.monkcommerce.coupon.app.common.CouponCodeGenerator;
import com.monkcommerce.coupon.app.domain.coupons.BuyGetCoupon;
import com.monkcommerce.coupon.app.domain.coupons.CartCoupon;
import com.monkcommerce.coupon.app.domain.coupons.Coupon;
import com.monkcommerce.coupon.app.domain.coupons.ProductCoupon;
import com.monkcommerce.coupon.app.repository.coupon.CouponRepository;
import com.monkcommerce.coupon.app.web.dto.request.coupon.CartRequestDTO;
import com.monkcommerce.coupon.app.web.dto.request.coupon.CreateCouponRequestDTO;
import com.monkcommerce.coupon.app.web.dto.request.coupon.UpdateCouponRequestDTO;
import com.monkcommerce.coupon.app.web.dto.response.coupon.CartResponseDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CouponService {

    @NonNull
    private final CouponRepository couponRepository;

    @NonNull
    private final CouponIDGeneratorService couponIDGeneratorService;

    public Coupon createCoupon(CreateCouponRequestDTO requestDTO) throws RuntimeException {
        if (requestDTO == null) {
            throw new RuntimeException("DTO is null.");
        }

        String type = requestDTO.getType();

        if (StringUtils.isEmpty(type)) {
            throw new RuntimeException("Coupon type is null to create coupon.");
        }

        return switch (type.toUpperCase()) {
            case "BUYGET" -> {
                validateBuyGetCoupon(requestDTO);
                yield saveBuyGetCoupon(requestDTO);
            }
            case "PRODUCT" -> {
                validateProductCoupon(requestDTO);
                yield saveProductCoupon(requestDTO);
            }
            case "CART" -> {
                validateCartCoupon(requestDTO);
                yield saveCartCoupon(requestDTO);
            }
            default -> throw new IllegalArgumentException("Invalid coupon type: " + type);
        };
    }

    private void validateBuyGetCoupon(CreateCouponRequestDTO dto) {
        Optional<BuyGetCoupon> existingCoupon = couponRepository.findActiveBuyGetCoupon(
            dto.getBuyQuantity(), dto.getBuyProductId(), dto.getGetQuantity(), dto.getGetProductId());
        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("An active BUYGET coupon with the same criteria already exists.");
        }
    }

    private void validateProductCoupon(CreateCouponRequestDTO dto) {
        Optional<ProductCoupon> existingCoupon = couponRepository.findActiveProductCoupon(dto.getProductId(),
                                                                                          dto.getDiscount());
        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("An active PRODUCT coupon for the same product already exists.");
        }
    }

    private void validateCartCoupon(CreateCouponRequestDTO dto) {
        Optional<CartCoupon> existingCoupon = couponRepository.findActiveCartCoupon(dto.getCartValue(),
                                                                                    dto.getDiscount());
        if (existingCoupon.isPresent()) {
            throw new IllegalArgumentException("An active CART coupon for the same cart value already exists.");
        }
    }

    private BuyGetCoupon saveBuyGetCoupon(CreateCouponRequestDTO dto) {
        BuyGetCoupon coupon = new BuyGetCoupon();
        coupon.setBuyQuantity(dto.getBuyQuantity());
        coupon.setBuyProductId(dto.getBuyProductId());
        coupon.setGetQuantity(dto.getGetQuantity());
        coupon.setGetProductId(dto.getGetProductId());
        return saveCommonFields(coupon, dto);
    }

    private ProductCoupon saveProductCoupon(CreateCouponRequestDTO dto) {
        ProductCoupon coupon = new ProductCoupon();
        coupon.setProductId(dto.getProductId());
        coupon.setDiscount(dto.getDiscount());
        return saveCommonFields(coupon, dto);
    }

    private CartCoupon saveCartCoupon(CreateCouponRequestDTO dto) {
        CartCoupon coupon = new CartCoupon();
        coupon.setCartValue(dto.getCartValue());
        coupon.setDiscount(dto.getDiscount());
        return saveCommonFields(coupon, dto);
    }

    private <T extends Coupon> T saveCommonFields(T coupon, CreateCouponRequestDTO dto) {
        String couponId = generateCouponId(dto.getType());
        String couponCode = StringUtils.isNotEmpty(
            dto.getCouponCode()) ? dto.getCouponCode() : generateUniqueCouponCode();

        coupon.setCouponId(couponId);
        coupon.setCouponCode(couponCode);
        coupon.setName(dto.getName());
        coupon.setActive(dto.isActive());
        coupon.setCreatedAt(System.currentTimeMillis());
        coupon.setUpdatedAt(System.currentTimeMillis());
        return couponRepository.save(coupon);
    }

    private String generateCouponId(String type) {
        long sequence = couponIDGeneratorService.generateSequence("coupon");
        return type.toUpperCase() + "-" + String.format("%05d", sequence);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    private String generateUniqueCouponCode() {
        String couponCode;
        int retryCount = 0;
        do {
            if (retryCount > 10) {
                throw new RuntimeException("Failed to generate a unique coupon code after multiple attempts");
            }
            couponCode = CouponCodeGenerator.generateCouponCode();
            retryCount++;
        } while (couponRepository.existsByCouponCode(couponCode));

        return couponCode;
    }

    public Coupon getCouponById(String couponId) {
        Optional<Coupon> coupon = couponRepository.findById(couponId);

        if (coupon.isEmpty()) {
            throw new RuntimeException("Coupon not found");
        }

        return coupon.get();
    }

    public Coupon updateCoupon(String id, UpdateCouponRequestDTO requestDTO) {
        Optional<Coupon> couponOptional = couponRepository.findById(id);

        if (couponOptional.isEmpty()) {
            throw new RuntimeException("Coupon with ID " + id + " not found");
        }

        Coupon coupon = getUpdatedCouponCode(requestDTO, couponOptional.get());
        return couponRepository.save(coupon);
    }

    private static Coupon getUpdatedCouponCode(UpdateCouponRequestDTO requestDTO, Coupon coupon) {
        if (requestDTO.getCouponCode() != null) {
            coupon.setCouponCode(requestDTO.getCouponCode());
        }

        if (requestDTO.getActive() != null) {
            coupon.setActive(requestDTO.getActive());
        }

        if (coupon instanceof BuyGetCoupon buyGetCoupon && requestDTO.getBuyQuantity() != null) {
            buyGetCoupon.setBuyQuantity(requestDTO.getBuyQuantity());
            buyGetCoupon.setBuyProductId(requestDTO.getBuyProductId());
            buyGetCoupon.setGetQuantity(requestDTO.getGetQuantity());
            buyGetCoupon.setGetProductId(requestDTO.getGetProductId());
        } else if (coupon instanceof ProductCoupon productCoupon && requestDTO.getProductId() != null) {
            productCoupon.setProductId(requestDTO.getProductId());
            productCoupon.setDiscount(requestDTO.getDiscount());
        } else if (coupon instanceof CartCoupon cartCoupon && requestDTO.getCartValue() != null) {
            cartCoupon.setCartValue(requestDTO.getCartValue());
            cartCoupon.setDiscount(requestDTO.getDiscount());
        }
        return coupon;
    }

    public void deleteCouponById(String id) {
        couponRepository.deleteById(id);
    }

    public List<Coupon> findApplicableCoupons(CartRequestDTO cartRequest) {
        try {
            List<CartRequestDTO.CartItemDTO> cartItems = cartRequest.getCart().getItems();
            double totalCartValue = cartItems.stream()
                                             .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                             .sum();

            Set<Long> cartProductIds = cartItems.stream()
                                                .map(CartRequestDTO.CartItemDTO::getProductId)
                                                .collect(Collectors.toSet());

            List<Coupon> activeCoupons = couponRepository.findByActiveTrue();

            return activeCoupons.stream()
                                .filter(coupon -> isCouponApplicable(coupon, cartItems, totalCartValue, cartProductIds))
                                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching applicable coupons: " + e.getMessage());
        }
    }

    public CartResponseDTO applyBestCoupon(CartRequestDTO cartRequest) {
        try {
            List<CartRequestDTO.CartItemDTO> cartItems = cartRequest.getCart().getItems();

            double totalPrice = cartItems.stream()
                                         .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                         .sum();

            Set<Long> cartProductIds = cartItems.stream()
                                                .map(CartRequestDTO.CartItemDTO::getProductId)
                                                .collect(Collectors.toSet());

            List<Coupon> activeCoupons = couponRepository.findByActiveTrue();

            List<Coupon> applicableCoupons = activeCoupons.stream()
                                                          .filter(coupon -> isCouponApplicable(coupon, cartItems,
                                                                                               totalPrice,
                                                                                               cartProductIds))
                                                          .toList();

            if (applicableCoupons.isEmpty()) {
                return null;
            }

            Coupon bestCoupon = null;
            double maxDiscount = 0.0;

            for (Coupon coupon : applicableCoupons) {
                double discount = calculateDiscount(coupon, cartItems, totalPrice);
                if (discount > maxDiscount) {
                    maxDiscount = discount;
                    bestCoupon = coupon;
                }
            }

            double finalPrice = totalPrice - maxDiscount;

            return CartResponseDTO.builder()
                                  .items(cartItems)
                                  .totalPrice(totalPrice)
                                  .totalDiscount(maxDiscount)
                                  .finalPrice(finalPrice)
                                  .appliedCoupon(bestCoupon != null ? CartResponseDTO.CouponDTO.builder()
                                                                                               .couponCode(
                                                                                                   bestCoupon.getCouponCode())
                                                                                               .type(
                                                                                                   bestCoupon.getType())
                                                                                               .name(
                                                                                                   bestCoupon.getName())
                                                                                               .discount(maxDiscount)
                                                                                               .build() : null)
                                  .build();

        } catch (Exception e) {
            throw new RuntimeException("Error while applying the best coupon: {}" + e.getMessage());
        }
    }

    public CartResponseDTO applySpecificCoupon(String couponCode, CartRequestDTO cartRequest) {
        try {
            Optional<Coupon> couponOptional = couponRepository.findByCouponCode(couponCode);

            if (couponOptional.isEmpty()) {
                return null;
            }

            Coupon coupon = couponOptional.get();

            if (!coupon.isActive()) {
                return null;
            }

            List<CartRequestDTO.CartItemDTO> cartItems = cartRequest.getCart().getItems();
            double totalPrice = cartItems.stream()
                                         .mapToDouble(item -> item.getPrice() * item.getQuantity())
                                         .sum();
            Set<Long> cartProductIds = cartItems.stream()
                                                .map(CartRequestDTO.CartItemDTO::getProductId)
                                                .collect(Collectors.toSet());

            boolean isApplicable = isCouponApplicable(coupon, cartItems, totalPrice, cartProductIds);

            if (!isApplicable) {
                return null;
            }

            double discount = calculateDiscount(coupon, cartItems, totalPrice);
            double finalPrice = totalPrice - discount;

            return CartResponseDTO.builder()
                                                          .items(cartItems)
                                                          .totalPrice(totalPrice)
                                                          .totalDiscount(discount)
                                                          .finalPrice(finalPrice)
                                                          .appliedCoupon(CartResponseDTO.CouponDTO.builder()
                                                                                                  .couponCode(coupon.getCouponCode())
                                                                                                  .type(coupon.getType())
                                                                                                  .name(coupon.getName())
                                                                                                  .discount(discount)
                                                                                                  .build())
                                                          .build();

        } catch (Exception e) {
            log.error("Error while applying specific coupon: {}", e.getMessage(), e);
            throw new RuntimeException("Error while applying specific coupon: " + e.getMessage());
        }
    }

    private boolean isCouponApplicable(Coupon coupon, List<CartRequestDTO.CartItemDTO> cartItems, double totalCartValue,
                                       Set<Long> cartProductIds) {
        if (coupon instanceof CartCoupon) {
            return totalCartValue >= ((CartCoupon) coupon).getCartValue();
        }

        if (coupon instanceof ProductCoupon) {
            return cartProductIds.contains(((ProductCoupon) coupon).getProductId());
        }

        if (coupon instanceof BuyGetCoupon buyGetCoupon) {
            return cartItems.stream().anyMatch(item ->
                                                   item.getProductId().equals(buyGetCoupon.getBuyProductId()) &&
                                                   item.getQuantity() >= buyGetCoupon.getBuyQuantity());
        }

        return false;
    }

    private double calculateDiscount(Coupon coupon, List<CartRequestDTO.CartItemDTO> cartItems, double totalCartValue) {
        if (coupon instanceof CartCoupon) {
            return ((CartCoupon) coupon).getDiscount(); // Flat discount
        }

        if (coupon instanceof ProductCoupon productCoupon) {
            Optional<CartRequestDTO.CartItemDTO> productItem = cartItems.stream()
                                                                        .filter(item -> item.getProductId().equals(
                                                                            productCoupon.getProductId()))
                                                                        .findFirst();
            return productItem.map(item -> (productCoupon.getDiscount() / 100) * (item.getPrice() * item.getQuantity()))
                              .orElse(0.0);
        }

        if (coupon instanceof BuyGetCoupon buyGetCoupon) {
            Optional<CartRequestDTO.CartItemDTO> freeProduct = cartItems.stream()
                                                                        .filter(item -> item.getProductId().equals(
                                                                            buyGetCoupon.getGetProductId()))
                                                                        .findFirst();
            return freeProduct.map(item -> item.getPrice() * buyGetCoupon.getGetQuantity()).orElse(0.0);
        }

        return 0.0;
    }
}
