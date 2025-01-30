package com.monkcommerce.coupon.app.web.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class CommonResponseDTO {
    private Object data;
    private String status;
    private String message;
}
