package com.bangvan.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String ward;
    private String district;
    private String province;
    private String country;
}