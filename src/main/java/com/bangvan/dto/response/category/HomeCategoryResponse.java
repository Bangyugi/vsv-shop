package com.bangvan.dto.response.category;
import com.bangvan.utils.HomeCategorySection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeCategoryResponse {
    private Long id;
    private String name;
    private String image;
    private String categoryId;
    private HomeCategorySection section;
    private boolean isActive;
}