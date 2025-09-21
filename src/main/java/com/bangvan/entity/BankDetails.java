package com.bangvan.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankDetails {

    String accountNumber;

    String accountHolderName;

    String bankName;

    String ifscCode;

}