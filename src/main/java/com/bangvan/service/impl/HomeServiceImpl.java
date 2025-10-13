package com.bangvan.service.impl;

import com.bangvan.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bangvan.entity.Deal;
import com.bangvan.entity.Home;
import com.bangvan.entity.HomeCategory;
import com.bangvan.repository.DealRepository;
import com.bangvan.repository.HomeCategoryRepository;

import com.bangvan.utils.HomeCategorySection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final HomeCategoryRepository homeCategoryRepository;
    private final DealRepository dealRepository;

    @Override
    public Home createHomePageData() {
        List<HomeCategory> allHomeCategories = homeCategoryRepository.findAll();
        List<HomeCategory> grid = filterByCategorySection(allHomeCategories, HomeCategorySection.GRID);
        List<HomeCategory> shopByCategories = filterByCategorySection(allHomeCategories, HomeCategorySection.SHOP_BY_CATEGORIES);
        List<HomeCategory> electricCategories = filterByCategorySection(allHomeCategories, HomeCategorySection.ELECTRIC_CATEGORIES);
        List<HomeCategory> dealCategories = filterByCategorySection(allHomeCategories, HomeCategorySection.DEALS);
        List<Deal> deals = dealRepository.findAll();
        return new Home(grid, shopByCategories, electricCategories, dealCategories, deals);
    }

    private List<HomeCategory> filterByCategorySection(List<HomeCategory> categories, HomeCategorySection section) {
        return categories.stream()
                .filter(category -> category.getSection() == section)
                .collect(Collectors.toList());
    }
}
