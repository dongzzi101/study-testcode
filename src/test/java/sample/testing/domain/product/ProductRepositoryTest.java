package sample.testing.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.testing.domain.product.ProductSellingStatus.*;
import static sample.testing.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
//@SpringBootTest
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    void findAllBySellingStatusIn() {
        // given
        Product product1 = createProduct("001", "아메리카노", 4000, SELLING, HANDMADE);
        Product product2 = createProduct("002", "카페라떼", 4500, HOLD, HANDMADE);
        Product product3 = createProduct("003", "팥빙수", 7000, STOP_SELLING, HANDMADE);

        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );

    }

    @Test
    @DisplayName("상품번호들로 상품들을 조회한다.")
    void findAllByProductNumberIn() {
        // given
        Product product1 = createProduct("001", "아메리카노", 4000, SELLING, HANDMADE);
        Product product2 = createProduct("002", "카페라떼", 4500, HOLD, HANDMADE);
        Product product3 = createProduct("003", "팥빙수", 7000, STOP_SELLING, HANDMADE);

        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );

    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 가져온다.")
    void findLastestProductNumber() {
        // given
        String targetProductNumber = "003";

        Product product1 = createProduct("001", "아메리카노", 4000, SELLING, HANDMADE);
        Product product2 = createProduct("002", "카페라떼", 4500, HOLD, HANDMADE);
        Product product3 = createProduct(targetProductNumber, "팥빙수", 7000, STOP_SELLING, HANDMADE);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        String lastestProductNumber = productRepository.findLastestProductNumber();

        // then
        assertThat(lastestProductNumber).isEqualTo(targetProductNumber);
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 가져올 때, 상품이 하나도 없는 경우에는 null을 반환한다.")
    void findLastestProductNumberWhenProductIsEmpty() {
        // when
        String lastestProductNumber = productRepository.findLastestProductNumber();

        // then
        assertThat(lastestProductNumber).isNull();
    }

    private Product createProduct(String productNumber, String name, int price, ProductSellingStatus productSellingStatus, ProductType productType) {
        return Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .sellingStatus(productSellingStatus)
                .name(name)
                .price(price)
                .build();
    }


}