package com.bangvan.dto.response.seller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateSellerStatusRequest {
    @NotBlank(message = "Status cannot be blank")
    private String status;
}