package com.bangvan.dto.request.seller;

import com.bangvan.entity.Address;
import com.bangvan.entity.BankDetails;
import com.bangvan.entity.BusinessDetails;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSellerRequest {
    @NotNull
    private BusinessDetails businessDetails;

    @NotNull
    private BankDetails bankDetails;

    @NotNull
    private Address pickupAddress;

    @NotNull
    private String gstin;
}
