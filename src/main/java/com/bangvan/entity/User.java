package com.bangvan.entity;

import com.bangvan.utils.Gender;
import com.bangvan.utils.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class User extends  AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    Long userId;

    @Column(name="username",unique = true,nullable = false)
    String username;

    @Column(name="password",nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Column(name="email",unique = true,nullable = false)
    String email;

    @Column(name="phone",unique = true,nullable = false)
    String phone;

    @Column(name="first_name",nullable = false)
    String firstName;

    @Column(name="last_name",nullable = false)
    String lastName;



    @Column(name = "avatar")
    String avatar = "https://cdn-icons-png.flaticon.com/512/3607/3607444.png";

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;

    @Column(name = "enabled")
    Boolean enabled = false;

    @Column(name="birth_date")
    LocalDate birthDate ;


    @Enumerated(EnumType.STRING)
    @Column(name="status")
    Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> role= new ArrayList<>();
        for (Role x: roles){
            role.add(new SimpleGrantedAuthority(x.getName()));
        }
        return role;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Serial
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
