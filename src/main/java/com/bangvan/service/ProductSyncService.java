package com.bangvan.service;

import com.bangvan.document.ProductDocument;
import com.bangvan.entity.Product;
import com.bangvan.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSyncService {

    private final ProductSearchRepository productSearchRepository;

    public void syncProductToElasticsearch(Product product) {
        try {

            String avatar = "";
            if (product.getSeller() != null && product.getSeller().getUser() != null) {
                avatar = product.getSeller().getUser().getAvatar();
            }


            String sellerName = "";
            if (product.getSeller() != null && product.getSeller().getBusinessDetails() != null) {
                sellerName = product.getSeller().getBusinessDetails().getBusinessName();
            }

            ProductDocument document = ProductDocument.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .sellingPrice(product.getSellingPrice())


                    .discountPercent(product.getDiscountPercent())
                    .numRatings(product.getNumRatings())
                    .images(product.getImages() != null ? product.getImages() : new ArrayList<>())
                    .sellerAvatar(avatar)


                    .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                    .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                    .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
                    .sellerName(sellerName)


                    .imageUrl(product.getImages() != null && !product.getImages().isEmpty()
                            ? product.getImages().get(0) : "")
                    .totalSold(product.getTotalSold())
                    .createdAt(product.getCreatedAt())
                    .build();

            productSearchRepository.save(document);
            log.info("Synced product id {} to Elasticsearch", product.getId());
        } catch (Exception e) {
            log.error("Failed to sync product id {} to Elasticsearch", product.getId(), e);
        }
    }

    public void deleteProductFromElasticsearch(Long productId) {
        productSearchRepository.deleteById(productId);
        log.info("Deleted product id {} from Elasticsearch", productId);
    }
}