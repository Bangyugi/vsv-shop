package com.bangvan.dto.request.seller;

import com.bangvan.entity.Address;
import com.bangvan.entity.BankDetails;
import com.bangvan.entity.BusinessDetails;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BecomeSellerRequest {
    @NotNull
    private BusinessDetails businessDetails;

    @NotNull
    private BankDetails bankDetails;

    @NotNull
    private Address pickupAddress;

    @NotNull
    private String gstin;
}
