package com.bangvan.dto.response.wishlist;


import com.bangvan.entity.Product;
import com.bangvan.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class WishListResponse {
    private Long id;
    private User user;
    private Set<Product> products;
}