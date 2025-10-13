package com.bangvan.entity;

import com.bangvan.utils.HomeCategorySection;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "home_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String image;

    String categoryId;

    @Enumerated(EnumType.STRING)
    HomeCategorySection section;

}