package com.monkcommerce.coupon.app.web.api;

import java.util.List;

import com.monkcommerce.coupon.app.domain.coupons.Coupon;
import com.monkcommerce.coupon.app.service.coupon.CouponService;
import com.monkcommerce.coupon.app.web.dto.request.coupon.CartRequestDTO;
import com.monkcommerce.coupon.app.web.dto.request.coupon.CreateCouponRequestDTO;
import com.monkcommerce.coupon.app.web.dto.request.coupon.UpdateCouponRequestDTO;
import com.monkcommerce.coupon.app.web.dto.response.common.CommonResponseDTO;
import com.monkcommerce.coupon.app.web.dto.response.coupon.CartResponseDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CouponEndpoint {

    @NonNull
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CommonResponseDTO> createCoupon(@RequestBody CreateCouponRequestDTO requestDTO) {
        CommonResponseDTO dto;
        try {
            Coupon savedCoupon = couponService.createCoupon(requestDTO);
            dto = CommonResponseDTO.builder()
                                   .data(savedCoupon)
                                   .message("OK")
                                   .status("OK")
                                   .build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED")
                                   .build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponseDTO> getAllCoupons() {
        CommonResponseDTO dto;
        try {
            List<Coupon> coupons = couponService.getAllCoupons();
            dto = CommonResponseDTO.builder()
                                   .data(coupons)
                                   .message("OK")
                                   .status("OK")
                                   .build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED")
                                   .build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDTO> getCouponById(@PathVariable("id") String id) {
        CommonResponseDTO dto;
        try {
            Coupon coupon = couponService.getCouponById(id);
            dto = CommonResponseDTO.builder()
                                   .data(coupon)
                                   .message("OK")
                                   .status("OK")
                                   .build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED")
                                   .build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDTO> updateCoupon(@PathVariable String id,
                                                          @RequestBody UpdateCouponRequestDTO requestDTO) {
        CommonResponseDTO dto;
        try {
            Coupon coupon = couponService.updateCoupon(id, requestDTO);
            dto = CommonResponseDTO.builder()
                                   .data(coupon)
                                   .message("OK")
                                   .status("OK")
                                   .build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED")
                                   .build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDTO> deleteCoupon(@PathVariable String id) {
        CommonResponseDTO dto;
        try {
            couponService.deleteCouponById(id);
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message("Coupon deleted successfully")
                                   .status("OK").build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED").build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<CommonResponseDTO> getApplicableCoupons(@RequestBody CartRequestDTO cartRequest) {
        CommonResponseDTO dto;
        try {
            List<Coupon> applicableCoupons = couponService.findApplicableCoupons(cartRequest);
            dto = CommonResponseDTO.builder()
                                   .data(applicableCoupons)
                                   .message("OK")
                                   .status("OK").build();

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED").build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @PostMapping("/apply-coupon/{coupon}")
    public ResponseEntity<CommonResponseDTO> applyCoupon(@RequestBody CartRequestDTO cartRequestDTO, @PathVariable String coupon) {
        CommonResponseDTO dto;

        try {
            CartResponseDTO responseDTO = couponService.applySpecificCoupon(coupon, cartRequestDTO);

            if (responseDTO == null) {
                dto = CommonResponseDTO.builder()
                    .data(null)
                    .message("Coupon couldn't be applied")
                    .status("FAILED").build();
            } else {
                dto = CommonResponseDTO.builder()
                                       .data(responseDTO)
                                       .message("OK")
                                       .status("OK").build();
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED").build();

            return ResponseEntity.badRequest().body(dto);
        }
    }

    @PostMapping("/apply-best-coupon/")
    public ResponseEntity<CommonResponseDTO> applyBestCoupon(@RequestBody CartRequestDTO cartRequestDTO) {
        CommonResponseDTO dto;

        try {
            CartResponseDTO responseDTO = couponService.applyBestCoupon(cartRequestDTO);

            if (responseDTO == null) {
                dto = CommonResponseDTO.builder()
                                       .data(null)
                                       .message("Coupon couldn't be applied")
                                       .status("FAILED").build();
            } else {
                dto = CommonResponseDTO.builder()
                                       .data(responseDTO)
                                       .message("OK")
                                       .status("OK").build();
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            dto = CommonResponseDTO.builder()
                                   .data(null)
                                   .message(e.getMessage())
                                   .status("FAILED").build();

            return ResponseEntity.badRequest().body(dto);
        }
    }
}
