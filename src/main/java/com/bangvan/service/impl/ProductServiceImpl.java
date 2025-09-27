package com.bangvan.service.impl;

import com.bangvan.dto.request.product.CreateProductRequest;
import com.bangvan.dto.request.product.UpdateProductRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.product.ProductResponse;
import com.bangvan.entity.Category;
import com.bangvan.entity.Product;
import com.bangvan.entity.Seller;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CategoryRepository;
import com.bangvan.repository.ProductRepository;
import com.bangvan.repository.SellerRepository;
import com.bangvan.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
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

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request, Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", request.getCategoryId()));
        Product product = modelMapper.map(request, Product.class);
        product.setSeller(seller);
        product.setCategory(category);
        product.setInStock(product.getQuantity()>0);

        product = productRepository.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public PageCustomResponse<ProductResponse> getAllProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> productPage;
        if (minPrice != null && maxPrice != null) {
            productPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class)
                )
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
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        modelMapper.map(request, product);
        if(request.getQuantity() != null){
            product.setInStock(request.getQuantity() > 0);
        }
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponse.class);

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
    public ProductResponse updateProductStock(Long productId, Integer quantity, Principal principal) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        product.setQuantity(quantity);
        product.setInStock(quantity > 0);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponse.class);
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
