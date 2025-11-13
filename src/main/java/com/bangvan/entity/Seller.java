package com.bangvan.entity;

import com.bangvan.utils.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seller extends AbstractEntity{

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Embedded
    BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    BankDetails bankDetails = new BankDetails();

    @OneToOne
    Address pickupAddress ;

    String gstin;

    Boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;


    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
            Set<Notification> notifications = new HashSet<>();

}