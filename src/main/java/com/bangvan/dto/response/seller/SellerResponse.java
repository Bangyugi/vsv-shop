package com.bangvan.dto.response.seller;

import com.bangvan.dto.response.user.UserResponse; // <-- THAY ĐỔI IMPORT
import com.bangvan.entity.Address;
import com.bangvan.entity.BankDetails;
import com.bangvan.entity.BusinessDetails;
// import com.bangvan.entity.User; // <-- BỎ IMPORT ENTITY
import com.bangvan.utils.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerResponse {
    private UserResponse user;
    private String avatar;
    private BusinessDetails businessDetails;
    private BankDetails bankDetails;
    private Address pickupAddress;
    private String gstin;
    private Boolean isEmailVerified;
    private AccountStatus accountStatus;

}