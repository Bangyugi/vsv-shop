package com.bangvan.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageCustomResponse<T> {
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    List<T> pageContent;
}
