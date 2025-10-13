package com.bangvan.dto.request.category;

import com.bangvan.utils.HomeCategorySection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class HomeCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    @NotBlank(message = "URL hình ảnh không được để trống")
    private String image;

    private String categoryId;

    @NotNull(message = "Khu vực hiển thị không được để trống")
    private HomeCategorySection section;

    private boolean isActive = true;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {

        @NotBlank(message = "Category name is required")
        private String name;

        private Long parentCategoryId;

        @NotNull(message = "Category level is required")
        private Integer level;
    }
}
