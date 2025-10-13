package com.bangvan.dto.response.category;

import com.bangvan.dto.response.category.HomeCategoryResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealResponse {
    private Long id;
    private Integer discount;
    private HomeCategoryResponse category;
}