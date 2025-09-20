package com.bangvan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
public class AbstractEntity {

    @Column(name = "created_at")
    @CreationTimestamp
    String createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    String updatedAt;

}
