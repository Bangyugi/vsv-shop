package com.bangvan.entity;

import com.bangvan.utils.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seller extends User {

    @Embedded
    BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    Address pickupAddress ;

    String gstin;

    Boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

}