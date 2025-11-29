package com.bangvan.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public  class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private Long parentCategoryId;

    private String image;

    private Boolean isActive;
}

