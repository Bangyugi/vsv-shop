package com.bangvan.dto.request.auth;


import com.bangvan.utils.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String birthDate;

    private String avatar = "https://cdn-icons-png.flaticon.com/512/3607/3607444.png";

    private Gender gender;

    // Trường này dùng để phân biệt loại user (STUDENT, ADVISOR)
    @NotBlank
    private String userType;

    // Các trường bổ sung dành riêng cho Student
    private String majorName;
    private LocalDate graduationTime;

    // Các trường bổ sung dành riêng cho Advisor
    private String facultyName;
    private String academicDegree;
}
