package com.bangvan.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {


    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")

    private String phoneNumber;

    @Email(message = "Định dạng email không hợp lệ")

    private String email;

    @NotBlank(message = "Địa chỉ (Đường, số nhà) không được để trống")
    private String address;


    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;


    private String country;


    private String note;

}