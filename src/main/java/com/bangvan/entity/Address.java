package com.bangvan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @Column(name = "full_name", nullable = false)
    String fullName;

    @Column(name = "phone_number", nullable = false)
    String phoneNumber;

    @Email
    @Column(name = "email")
    String email;


    @Column(name = "address", nullable = false)
    String address;


    @Column(name = "district", nullable = false)
    String district;

    @Column(name = "province", nullable = false)
    String province;

    @Column(name = "country")
    String country = "Việt Nam";


    @Column(name = "note")
    String note;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}