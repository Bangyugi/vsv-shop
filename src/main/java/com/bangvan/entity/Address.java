package com.bangvan.entity;

import jakarta.persistence.*;
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

    @Column(name = "address_line_1", nullable = false)
    String addressLine1;

    @Column(name = "address_line_2")
    String addressLine2;

    @Column(name = "ward")
    String ward;

    @Column(name = "district")
    String district;

    @Column(name = "province")
    String province;

    @Column(name = "country")
    String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}