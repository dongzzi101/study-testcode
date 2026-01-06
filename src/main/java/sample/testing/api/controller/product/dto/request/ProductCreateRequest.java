package sample.testing.api.controller.product.dto.request;

import lombok.Builder;
import lombok.Getter;
import sample.testing.domain.product.Product;
import sample.testing.domain.product.ProductSellingStatus;
import sample.testing.domain.product.ProductType;

@Getter
public class ProductCreateRequest {

    private ProductType type;
    private ProductSellingStatus sellingStatus;
    private String name;
    private int price;

    @Builder
    public ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public Product toEntity(String nextProductNumber) {
        return Product.builder()
                .productNumber(nextProductNumber)
                .name(name)
                .price(price)
                .sellingStatus(sellingStatus)
                .type(type)
                .build();
    }
}


