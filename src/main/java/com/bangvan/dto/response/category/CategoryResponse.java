package com.bangvan.dto.response.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryResponse parentCategory;
    private Integer level;
}