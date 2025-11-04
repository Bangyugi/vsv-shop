package com.bangvan.dto.response.user; // Đảm bảo đúng package

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String district;
    private String province;
    private String country;
    private String note;
}