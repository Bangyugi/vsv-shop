package com.bangvan.dto.response.seller;

import com.bangvan.entity.Address;
import com.bangvan.entity.BankDetails;
import com.bangvan.entity.BusinessDetails;
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
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatar;

    // Seller fields
    private BusinessDetails businessDetails;
    private BankDetails bankDetails;
    private Address pickupAddress;
    private String gstin;
    private Boolean isEmailVerified;
    private AccountStatus accountStatus;

}
