package com.bangvan.entity;

import com.bangvan.utils.PaymentStatus;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDetails {
    

    String paymentLinkId;

    String paymentLinkReferenceId;

    String paymentLinkStatus;

    String paymentId​;

    PaymentStatus status;

}