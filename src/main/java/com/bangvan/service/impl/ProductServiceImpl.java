package com.bangvan.service.impl;

import com.bangvan.dto.request.product.CreateProductRequest;
import com.bangvan.dto.request.product.UpdateProductRequest;
import com.bangvan.dto.request.product.UpdateStockRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.product.ProductResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CategoryRepository;
import com.bangvan.repository.ProductRepository;
import com.bangvan.repository.ProductVariantRepository;
import com.bangvan.repository.SellerRepository;
import com.bangvan.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository; // Thêm repository mới

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request, Principal principal) {
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", request.getCategoryId()));

        Product product = modelMapper.map(request, Product.class);
        product.setSeller(seller);
        product.setCategory(category);
        product.setVariants(new HashSet<>());


        for (ProductVariant variantRequest : request.getVariants()) {
            ProductVariant newVariant = new ProductVariant();
            newVariant.setColor(variantRequest.getColor());
            newVariant.setSize(variantRequest.getSize());
            newVariant.setQuantity(variantRequest.getQuantity());
            String sku = (product.getTitle() + "-" + variantRequest.getColor() + "-" + variantRequest.getSize())
                    .replaceAll("\\s+", "-").toUpperCase();
            newVariant.setSku(sku);
            newVariant.setProduct(product);
            product.getVariants().add(newVariant);
        }

        Product savedProduct = productRepository.save(product);
        return mapProductToResponse(savedProduct);
    }


    private ProductResponse mapProductToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setTotalQuantity(product.getTotalQuantity());
        return response;
    }


    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return mapProductToResponse(product);
    }

    @Override
    public PageCustomResponse<ProductResponse> getAllProducts(BigDecimal minPrice, BigDecimal maxPrice, String color, String size, Long sellerId, String keyword, Long categoryId, Pageable pageable) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (color != null && !color.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("variants").get("color"), color));
            }
            if (size != null && !size.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.join("variants").get("size"), size));
            }
            if (sellerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("seller").get("id"), sellerId));
            }
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        return PageCustomResponse.<ProductResponse>builder()
                .pageNo(productPage.getNumber() + 1)
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageContent(productResponses)
                .build();
    }

    @Transactional
    @Override
    public ProductResponse updateProductById(Long productId, UpdateProductRequest request, Principal principal) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Cập nhật các trường thông thường của sản phẩm một cách thủ công
        // để tránh ModelMapper thay thế collection
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDiscountPercent(request.getDiscountPercent());
        if(request.getImages() != null){
            product.setImages(request.getImages());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getVariants() != null) {
            product.getVariants().clear();

            for (ProductVariant variantRequest : request.getVariants()) {
                ProductVariant newVariant = new ProductVariant();
                newVariant.setColor(variantRequest.getColor());
                newVariant.setSize(variantRequest.getSize());
                newVariant.setQuantity(variantRequest.getQuantity());
                String sku = (product.getTitle() + "-" + variantRequest.getColor() + "-" + variantRequest.getSize())
                        .replaceAll("\\s+", "-").toUpperCase();
                newVariant.setSku(sku);
                newVariant.setProduct(product);
                product.getVariants().add(newVariant);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return mapProductToResponse(updatedProduct);
    }


    @Transactional
    @Override
    public String deleteProductById(Long productId, Principal principal) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        productRepository.delete(product);
        return "Product with ID " + productId + " has been deleted successfully.";
    }


    @Override
    public Integer calculateDiscountPercentage(BigDecimal price, BigDecimal sellingPrice) {
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }
        BigDecimal discount = price.subtract(sellingPrice);
        BigDecimal discountPercentage = discount.multiply(new BigDecimal("100")).divide(price, 0, RoundingMode.HALF_UP);
        return discountPercentage.intValue();
    }

    @Transactional
    @Override
    public ProductResponse updateProductStock(Long variantId, UpdateStockRequest request, Principal principal) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", variantId));
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));
        Product product = variant.getProduct();
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        variant.setQuantity(request.getQuantity());
        productVariantRepository.save(variant);
        Product updatedProduct = productRepository.findById(product.getId()).get();
        return mapProductToResponse(updatedProduct);
    }

    @Override
    public PageCustomResponse<ProductResponse> findProductBySeller(Long sellerId, Pageable pageable) {
        Page<Product> productPage = productRepository.findBySellerId(sellerId, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());

        return PageCustomResponse.<ProductResponse>builder()
                .pageNo(productPage.getNumber() + 1)
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageContent(productResponses)
                .build();
    }

    @Override
    public PageCustomResponse<ProductResponse> searchProduct(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());

        return PageCustomResponse.<ProductResponse>builder()
                .pageNo(productPage.getNumber() + 1)
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageContent(productResponses)
                .build();
    }
}