package com.bangvan.dto.response.user;
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
    Long id;
    String username;
    String email;
    String phone;
    String firstName;
    String lastName;
    String avatar;
    Gender gender;
    LocalDate birthDate;
    Boolean enabled;
    Set<Role> roles;
}