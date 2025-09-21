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
public class BusinessDetails {

    String businessName;

    String businessEmail;

    String businessMobile;

    String businessAddress;

    String logo;

    String banner;

}