package com.bangvan.dto.response.user;
import com.bangvan.dto.response.seller.SellerResponse;
import com.bangvan.entity.Role;
import com.bangvan.utils.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse  {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatar;
    private Gender gender;
    private LocalDate birthDate;
    private Boolean enabled;
    private Set<Role> roles;

}